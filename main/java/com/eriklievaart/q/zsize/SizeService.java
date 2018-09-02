package com.eriklievaart.q.zsize;

import java.util.function.Supplier;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.ui.api.QMainUi;

public class SizeService implements QPlugin {

	private final SizeController controller;

	public SizeService(Supplier<QMainUi> supplier) {
		this.controller = new SizeController(supplier);
	}

	@Override
	public Invokable createInstance() {
		return new SizeShellCommand(controller);
	}

	@Override
	public String getCommandName() {
		return "size";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.SINGLE;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.FLAGS_ONLY;
	}

}
