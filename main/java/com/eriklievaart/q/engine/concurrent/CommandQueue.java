package com.eriklievaart.q.engine.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.eriklievaart.q.engine.impl.PluginJob;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;

/**
 * This is the central queue in Q. Long running jobs are queued in the CommandQueue and executed one by one. The
 * CommandQueue also has methods for clearing or pausing the queue.
 *
 * @author Erik Lievaart
 */
public class CommandQueue {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private EngineSupplierFactory factory;

	public CommandQueue(EngineSupplierFactory factory) {
		this.factory = factory;
	}

	void invokeLater(final PluginJob job) {
		factory.getEngineUi().addJob(job);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					job.run();

				} finally {
					factory.getEngineUi().completeJob(job);
				}
			}
		});
	}
}
