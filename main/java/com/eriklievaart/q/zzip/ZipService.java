package com.eriklievaart.q.zzip;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.ui.api.QMainUi;

public class ZipService implements QPlugin {

	private ZipController controller;

	public ZipService(ServiceCollection<QMainUi> qui) {
		this.controller = new ZipController(qui);
	}

	@Override
	public String getCommandName() {
		return "zip";
	}

	@Override
	public Invokable createInstance() {
		return new ZipShellCommand(controller);
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.QUEUE;
	}
}