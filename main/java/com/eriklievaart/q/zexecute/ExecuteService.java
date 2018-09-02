package com.eriklievaart.q.zexecute;

import java.util.function.Supplier;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.ui.api.QMainUi;

public class ExecuteService implements QPlugin {
	private ExecuteController components;

	public ExecuteService(Supplier<QMainUi> supplier) {
		this.components = new ExecuteController(supplier);
	}

	@Override
	public Invokable createInstance() {
		return new ExecuteShellCommand(components);
	}

	@Override
	public String getCommandName() {
		return "execute";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.FORK;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.BOTH;
	}
}
