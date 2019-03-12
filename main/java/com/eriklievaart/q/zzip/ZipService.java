package com.eriklievaart.q.zzip;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class ZipService implements QPlugin {

	@Override
	public String getCommandName() {
		return "zip";
	}

	@Override
	public Invokable createInstance() {
		return new ZipShellCommand();
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.QUEUE;
	}
}
