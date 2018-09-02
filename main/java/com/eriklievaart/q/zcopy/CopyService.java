package com.eriklievaart.q.zcopy;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class CopyService implements QPlugin {
	@Override
	public Invokable createInstance() {
		return new CopyShellCommand();
	}

	@Override
	public String getCommandName() {
		return "copy";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.QUEUE;
	}
}
