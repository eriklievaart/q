package com.eriklievaart.q.api.engine;

/**
 * Enumeration of the different policies for concurrency that a ShellCommand can implement.
 *
 * @author Erik Lievaart
 */
public enum ThreadPolicy {

	/**
	 * Run all invocations of the command in a single dedicated Thread. This is not the Swing thread. The thread will be
	 * marked interrupted when a new invocation is queued. The developer of the plug in has to monitor the interrupted
	 * flag and throw a {@link SingleThreadInterruptedException} when appropriate.
	 */
	SINGLE,

	/**
	 * Run the command in the Swing event Thread. Use only for short running operations.
	 */
	SWING,

	/**
	 * Run the command in a freshly spawned Thread. Resource intensive and can cause concurrency issues, but causes
	 * quick results with no lockups.
	 */
	FORK,

	/**
	 * Queue the command for execution. Generally recommended for long running tasks. Queued tasks will be run one by
	 * one.
	 */
	QUEUE,

	/**
	 * Run the command in the current Thread, whichever Thread that may be. This policy is efficient because no Thread
	 * switching occurs, but should not be used for long running operations, because it could lock up the GUI.
	 */
	CURRENT;
}
