package com.eriklievaart.q.zfind.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FindController {

	private Supplier<QMainUi> ui;
	private Supplier<Engine> engine;

	public JPanel panel = new JPanel(new BorderLayout());
	public DefaultListModel<FindResult> model = new DefaultListModel<>();
	public JList<FindResult> list = new JList<>(model);
	public JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
	public JButton copyButton = new JButton("Copy Selection");
	public JButton moveButton = new JButton("Move Selection");
	public JButton deleteButton = new JButton("Delete Selection");

	public FindController(Supplier<QMainUi> ui, Supplier<Engine> engine) {
		this.ui = ui;
		this.engine = engine;
		initUi();
	}

	private void initUi() {
		list.setModel(model);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
		panel.add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);
		buttonPanel.add(copyButton);
		buttonPanel.add(moveButton);
		buttonPanel.add(deleteButton);

		bind(KeyStroke.getKeyStroke("F5"), "copy", e -> copy());
		bind(KeyStroke.getKeyStroke("F6"), "move", e -> move());
		bind(KeyStroke.getKeyStroke("DELETE"), "delete", e -> delete());
	}

	private void bind(KeyStroke key, String id, Consumer<ActionEvent> consumer) {
		list.getInputMap().put(key, id);
		list.getActionMap().put(id, action(id, consumer));
	}

	public AbstractAction action(String name, Consumer<ActionEvent> consumer) {
		return new AbstractAction(name) {
			@Override
			public void actionPerformed(ActionEvent e) {
				consumer.accept(e);
			}
		};
	}

	public void find() {
		ui.get().getDialogs().input("Search for files", input -> {
			engine.get().invoke(Str.sub("find -i `*$*`", UrlTool.escape(input)));
		});
	}

	public void copy() {
		contextAction("copy");
	}

	public void move() {
		contextAction("move");
	}

	private void contextAction(String action) {
		fileMessage(results -> {
			String msg = createContextQuestion(action + " to which location?");
			ui.get().getDialogs().choice(msg, new String[] { "$dir1", "$dir2" }, selected -> {
				engine.get().invoke(Str.sub("$ -u % $", action, results.getUrls(), selected));
				ui.get().showBrowser();
			});
		});
	}

	private String createContextQuestion(String question) {
		QContext context = ui.get().getQContext();
		VirtualFile dir1 = context.getLeft().getDirectory();
		VirtualFile dir2 = context.getRight().getDirectory();
		StringBuilder builder = new StringBuilder("<html>").append(question).append("<br/><br/>");
		builder.append("$dir1: ").append(dir1.getUrl().getUrlUnescaped());
		builder.append("<BR/>");
		builder.append("$dir2: ").append(dir2.getUrl().getUrlUnescaped());
		return builder.toString();
	}

	public void delete() {
		fileMessage(results -> {
			ui.get().getDialogs().confirm("delete files?", () -> {
				engine.get().invoke(Str.sub("delete -u %", results.getUrls()));
			});
		});
	}

	public void fileMessage(Consumer<FindResults> consumer) {
		List<FindResult> selection = list.getSelectedValuesList();
		if (selection.isEmpty()) {
			JOptionPane.showMessageDialog(panel, "No files selected");

		} else {
			consumer.accept(new FindResults(selection));
		}
	}

	public void cleanList() {
		for (int i = 0; i < model.getSize(); i++) {
			if (!model.get(i).getVirtualFile().exists()) {
				model.remove(i--);
			}
		}
	}

	public void open() {
		FindResult result = list.getSelectedValue();
		if (result == null) {
			return;
		}
		VirtualFile file = result.getVirtualFile();

		if (file.isDirectory()) {
			ui.get().navigateFuzzy("active", result.getVirtualFile().getUrl().getUrlUnescaped());
			ui.get().showBrowser();

		} else if (file instanceof SystemFile) {
			try {
				SystemFile sf = (SystemFile) file;
				Desktop.getDesktop().open(sf.unwrap());
			} catch (IOException e) {
				throw new RuntimeIOException(e);
			}
		}
	}

	public void parent() {
		FindResult result = list.getSelectedValue();
		if (result == null) {
			return;
		}
		ui.get().navigateFuzzy("active", result.getVirtualFile().getParentFile().get().getUrl().getUrlUnescaped());
		ui.get().showBrowser();
	}

	public void showResults(VirtualFile root, Iterator<VirtualFile> scan) {
		model.removeAllElements();
		final AtomicBoolean visible = new AtomicBoolean(false);
		int count = 0;

		ArrayBlockingQueue<FindResult> results = new ArrayBlockingQueue<>(8000);
		while (scan.hasNext()) {
			results.add(nextResult(root, scan));
			SwingThread.invokeLater(() -> {
				showInList(visible, results);
			});
			if (++count >= 8000) {
				JOptionPane.showMessageDialog(null, "find: too many results!");
				return;
			}
		}
		if (count == 0) {
			JOptionPane.showMessageDialog(null, "find: no matches found!");
		}
	}

	private FindResult nextResult(VirtualFile root, Iterator<VirtualFile> scan) {
		VirtualFile file = scan.next();
		String label = "." + file.getPath().substring(root.getPath().length());
		return new FindResult(file, label);
	}

	private void showInList(final AtomicBoolean visible, ArrayBlockingQueue<FindResult> results) {
		model.addElement(results.remove());

		QMainUi main = ui.get();
		if (main != null && !visible.get()) {
			QView view = new QView("q.find", panel);
			view.setLabel("find");
			main.showView(view);
			visible.set(true);
		}
	}

	public DefaultListModel<FindResult> getModel() {
		return model;
	}
}