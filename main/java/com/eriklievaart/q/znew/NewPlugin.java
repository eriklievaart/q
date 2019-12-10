package com.eriklievaart.q.znew;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class NewPlugin implements QPlugin {
	@Override
	public Invokable createInstance() {
		return new NewShellCommand();
	}

	@Override
	public String getCommandName() {
		return "new";
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