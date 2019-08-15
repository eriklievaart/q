package com.eriklievaart.q.zexecute;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.runtime.api.CliOutput;
import com.eriklievaart.toolkit.runtime.api.CliStreams;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class ExecuteController {

	private Supplier<QMainUi> ui;

	private JPanel panel = new JPanel(new GridLayout(1, 0));
	private JList<TerminalLine> list = new JList<>();
	private Vector<TerminalLine> lines = new Vector<>(1000);
	private AtomicBoolean changed = new AtomicBoolean(false);

	public ExecuteController(Supplier<QMainUi> supplier) {
		this.ui = supplier;
		panel.add(new JScrollPane(list));
		list.setBackground(new Color(16, 16, 31));
		list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
	}

	public void showPanel() {
		QMainUi controller = ui.get();
		if (controller != null) {
			list.setCellRenderer(controller.createListCellRenderer(new ExecuteColorFactory()));
			QView view = new QView("q.execute.terminal", panel);
			view.setLabel("terminal");
			controller.showView(view);
		}

	}

	public CliStreams createOutputStreams() {
		CliOutput out = new CliOutput() {
			@Override
			public void println(String line) {
				addLine(TerminalLine.normal(line));
			}
		};
		CliOutput err = new CliOutput() {
			@Override
			public void println(String line) {
				addLine(TerminalLine.error(line));
			}
		};
		return new CliStreams(out, err);
	}

	public void addLine(TerminalLine line) {
		lines.add(line);
		while (lines.size() > 1000) {
			lines.remove(0);
		}
		changed.set(true);
		SwingThread.invokeLater(() -> {
			if (changed.compareAndSet(true, false)) {
				TerminalLine[] data = lines.toArray(new TerminalLine[] {});
				list.setListData(data);
				list.ensureIndexIsVisible(data.length - 1);
			}
		});
	}

	public void printHeader(String raw) {
		String header = "## " + raw.trim() + " ##";
		String hashes = Str.repeat("#", header.length());

		addLine(TerminalLine.normal(hashes));
		addLine(TerminalLine.normal(header));
		addLine(TerminalLine.normal(hashes));
	}
}
