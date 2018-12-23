package com.eriklievaart.q.engine;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class DummyPlugin implements QPlugin {

	private String command;
	private Invokable invokable;
	private CallPolicy callPolicy = CallPolicy.BOTH;
	private ThreadPolicy threadPolicy = ThreadPolicy.CURRENT;

	public DummyPlugin(String command, Invokable invokable) {
		this.command = command;
		this.invokable = invokable;
	}

	public void setCallPolicy(CallPolicy value) {
		this.callPolicy = value;
	}

	@Override
	public String getCommandName() {
		return command;
	}

	@Override
	public Invokable createInstance() {
		return invokable;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return callPolicy;
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return threadPolicy;
	}
}
