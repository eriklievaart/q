package com.eriklievaart.q.api;

import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;

/**
 * A bundle that registers commands with the engine should implement a OSGI service with this interface.
 *
 * @author erikl
 */
public interface QPlugin {

	/**
	 * Returns the name under which the command should be made available in the engine.
	 */
	public String getCommandName();

	/**
	 * Returns the ThreadPolicy to use when calling this command.
	 */
	public default ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.SWING;
	}

	/**
	 * Return whether or not it is possible to pipe content to this command.
	 */
	public default CallPolicy getCallPolicy() {
		return CallPolicy.FLAGS_ONLY;
	}

	/**
	 * True iff the piped content should be a valid QPlugin command. Only relevant if plug in accepts piped content.
	 *
	 * @return true requires piped content to be valid QPlugin command, false for free format piped content.
	 */
	public default boolean isValidatePipedContent() {
		return true;
	}

	/**
	 * Create an Invokable or Swallows prototype.
	 */
	public Invokable createInstance();
}