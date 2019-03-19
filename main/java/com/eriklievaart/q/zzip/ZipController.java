package com.eriklievaart.q.zzip;

import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.ui.api.QMainUi;

public class ZipController {

	private ServiceCollection<QMainUi> main;

	public ZipController(ServiceCollection<QMainUi> qui) {
		this.main = qui;
	}

	public void show(List<String> paths) {
		JList<String> list = new JList<>(paths.toArray(new String[] {}));
		main.allCall(ui -> ui.showView(new QView("zip", new JScrollPane(list))));
	}
}