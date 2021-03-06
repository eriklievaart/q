package com.eriklievaart.q.zexecute;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.api.render.JListThemed;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.runtime.api.CliOutput;
import com.eriklievaart.toolkit.runtime.api.CliStreams;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class ExecuteController {

	private Supplier<QMainUi> ui;
	private AtomicBoolean changed = new AtomicBoolean(false);
	private Vector<TerminalLine> lines = new Vector<>(1000);

	JPanel panel = new JPanel(new GridLayout(1, 0));
	JListThemed<TerminalLine> list = new JListThemed<>();

	public ExecuteController(Supplier<QMainUi> supplier) {
		this.ui = supplier;
		panel.add(new JScrollPane(list));
	}

	public void showPanel() {
		QMainUi controller = ui.get();
		if (controller != null) {
			list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, list.getFont().getSize()));
			list.setForegroundFactory(new ExecuteColorFactory());
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

	public void each(Supplier<Engine> engine, boolean frame) {
		String prefill = frame ? "echo $name $path $!url" : "gvim $name";
		String input = JOptionPane.showInputDialog(null, "execute on each file", prefill);
		if (Str.notBlank(input)) {
			String command = frame ? "execute -ef | " : "execute -e | ";
			engine.get().invoke(command + input);
		}
	}
}