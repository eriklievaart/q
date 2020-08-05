package com.eriklievaart.q.zsize;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.zsize.dir.DirCalculation;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class SizeController {

	private Supplier<QMainUi> ui;

	JPanel panel = new JPanel(new BorderLayout());
	JList<String> list = new JList<>();
	JLabel summary = new JLabel();

	public SizeController(Supplier<QMainUi> supplier) {
		this.ui = supplier;
		panel.add(summary, BorderLayout.NORTH);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);
	}

	public void showResults(List<DirCalculation> sizes) {
		String[] data = new String[sizes.size()];
		for (int i = 0; i < sizes.size(); i++) {
			DirCalculation calculation = sizes.get(i);
			data[i] = formatLong(calculation.getSize()) + calculation.getName();
		}
		SwingThread.invokeLater(() -> list.setListData(data));
	}

	private String formatLong(long size) {
		return String.format("%,20d    ", new Object[] { size });
	}

	public void init() {
		list.setListData(new String[] {});
		list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, list.getFont().getSize()));
		QView view = new QView("q.size", panel);
		view.setLabel("dir sizes");
		ui.get().showView(view);
	}

	public void showLabel(VirtualFile dir, List<DirCalculation> sizes) {
		long total = sizes.stream().mapToLong(c -> c.getSize()).sum();
		int remaining = dir.getChildren().size() - sizes.size();
		String suffix = remaining == 0 ? "" : "(remaining:" + remaining + ")";
		summary.setText(dir.getPath() + " " + formatLong(total) + suffix);
	}
}