package com.eriklievaart.q.engine.api;

/**
 * Interface for checking and invoking ShellCommands. This is one of the central interfaces in the application.
 *
 * @author Erik Lievaart
 */
public interface Engine {

	/**
	 * Invoke a ShellCommand.
	 *
	 * @param raw
	 *            ShellCommand in raw form.
	 */
	public void invoke(String raw);

	/**
	 * Parse the command and verify correctness.
	 */
	public EngineResult parse(String verify);

	/**
	 * Apply engine templates and invoke ShellCommand.
	 */
	public void invokeTemplated(String raw);

	/**
	 * Apply templates, parse the command and verify correctness.
	 */
	public EngineResult parseTemplated(String verify);

	/**
	 * Give an estimate on how many jobs still need to complete.
	 */
	public long getQueuedJobCount();

}
