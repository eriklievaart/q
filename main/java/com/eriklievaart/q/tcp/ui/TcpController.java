package com.eriklievaart.q.tcp.ui;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class TcpController {

	private ServiceCollection<QMainUi> ui;

	private boolean visible = false;
	public JPanel panel = new JPanel(new BorderLayout());
	public DefaultListModel<String> model = new DefaultListModel<>();
	public JList<String> list = new JList<>(model);

	public TcpController(ServiceCollection<QMainUi> ui) {
		this.ui = ui;

		panel.add(new JScrollPane(list), BorderLayout.CENTER);
	}

	public void receivedRequest(final TunnelVO vo) {
		SwingThread.invokeLater(() -> {
			if (!model.isEmpty() && model.lastElement().equals(vo.args)) {
				return;
			}
			if (vo.command == TunnelCommand.INFO) {
				return;
			}
			if (vo.command == TunnelCommand.LS && sameAsLastListing(vo.getCommandLine())) {
				return;
			}
			model.addElement(vo.getCommandLine());
			showList();
		});

	}

	private boolean sameAsLastListing(String request) {
		int index = model.size();

		while (index-- > 0) {
			String line = model.getElementAt(index);
			if (line.startsWith("LS")) {
				return request.equals(line);
			}
		}
		return false;
	}

	private void showList() {
		ui.oneCall(main -> {
			if (!visible) {
				QView view = new QView("q.tcp", panel);
				view.setLabel("tcp");
				main.showView(view);
				visible = true;
			}
		});
	}

	public void cleanup() {
		visible = false;
		model.removeAllElements();
	}
}