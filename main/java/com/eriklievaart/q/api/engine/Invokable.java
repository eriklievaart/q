package com.eriklievaart.q.api.engine;

/**
 * ShellCommands that can be invoked without any swallowed content must implement this interface.
 *
 * @author Erik Lievaart
 */
public interface Invokable {

	/**
	 * Invoke the ShellCommand.
	 *
	 * @param context
	 *            invocation context.
	 * @throws Exception
	 *             any Exception can be thrown.
	 */
	public void invoke(PluginContext context) throws Exception;

	/**
	 * Validate the ShellCommand. This method is called after all flags have been set and before the command is invoked.
	 *
	 * @param context
	 *            invocation context.
	 * @throws PluginException
	 *             Signals that the current arguments are not valid.
	 */
	void validate(PluginContext context) throws PluginException;
}
