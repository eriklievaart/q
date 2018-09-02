package com.eriklievaart.q.zdelete;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

public class DeleteService implements QPlugin {
	private TrashCache cache = new TrashCache();

	@Override
	public Invokable createInstance() {
		return new DeleteShellCommand(cache);
	}

	@Override
	public String getCommandName() {
		return "delete";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.QUEUE;
	}
}