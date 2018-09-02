package com.eriklievaart.q.zmove;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class MoveService implements QPlugin {
	@Override
	public Invokable createInstance() {
		return new MoveShellCommand();
	}

	@Override
	public String getCommandName() {
		return "move";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.QUEUE;
	}
}