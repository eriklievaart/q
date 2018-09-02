package com.eriklievaart.q.zlocation;

import java.util.function.Supplier;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.ui.api.QMainUi;

public class LocationService implements QPlugin {
	private Supplier<QMainUi> supplier;

	public LocationService(Supplier<QMainUi> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Invokable createInstance() {
		return new LocationShellCommand(supplier);
	}

	@Override
	public String getCommandName() {
		return "location";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.SWING;
	}
}
