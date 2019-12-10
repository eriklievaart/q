package com.eriklievaart.q.engine.concurrent;

import java.util.Objects;

import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.engine.impl.PluginJob;
import com.eriklievaart.q.engine.impl.PluginRunner;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.swing.api.SwingThread;

/**
 * The CommandScheduler schedules ShellCommands for execution.
 *
 * @author Erik Lievaart
 */
public class CommandScheduler {

	private CommandQueue queue;
	private PluginRunner runner;
	private SingleThreadQueue single = new SingleThreadQueue();
	private boolean testing = Objects.equals("true", System.getProperty("q.test"));

	public CommandScheduler(EngineSupplierFactory factory) {
		runner = new PluginRunner(factory.getPluginIndex(), factory.getCollectionsConvertersSupplier());
		queue = new CommandQueue(factory);
	}

	/**
	 * Schedule a job for execution.
	 *
	 * @param job
	 *            Job to schedule.
	 * @param policy
	 *            Threading policy to use. This policy determines if the job will be executed immediately or queued for
	 *            execution.
	 */
	public void schedule(final PluginJob job, final ThreadPolicy policy) {
		job.setPluginRunner(runner);
		applyPolicy(job, testing ? ThreadPolicy.CURRENT : policy);
	}

	private void applyPolicy(final PluginJob job, final ThreadPolicy policy) {
		switch (policy) {

		case CURRENT:
			job.run();
			return;

		case SWING:
			SwingThread.invokeLater(job);
			return;

		case FORK:
			new Thread(job).start();
			return;

		case QUEUE:
			queue.invokeLater(job);
			return;

		case SINGLE:
			single.invokeLater(job.getCommandName(), job);
			return;
		}
		throw new AssertionException("Unknown policy: %", policy);
	}
}