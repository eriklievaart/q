package com.eriklievaart.q.engine.concurrent;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.eriklievaart.q.engine.impl.PluginJob;
import com.eriklievaart.toolkit.lang.api.collection.LazyMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

class SingleThreadQueue {

	private final Map<String, Future<?>> last = NewCollection.map();
	private final LazyMap<String, ExecutorService> executors = new LazyMap<>(key -> {
		return Executors.newSingleThreadExecutor();
	});

	public synchronized void invokeLater(String command, PluginJob runnable) {
		cancelLast(command);

		Future<?> future = executors.get(command).submit(runnable);
		last.put(command, future);
	}

	private void cancelLast(final String command) {
		Future<?> running = last.get(command);
		if (running != null) {
			running.cancel(true);
		}
	}
}