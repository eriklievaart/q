package com.eriklievaart.q.engine.api;

import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

/**
 * Generic message class.
 * 
 * @author Erik Lievaart
 */
@Immutable
public class EngineResult {

	private final String message;
	private final boolean error;

	private EngineResult(final String message, final boolean error) {
		this.message = message;
		this.error = error;
	}

	/**
	 * Factory method for creating Error messages.
	 * 
	 * @param msg
	 *            details of the error.
	 * @return the constructed message.
	 */
	public static EngineResult error(final String msg) {
		return new EngineResult(msg, true);
	}

	/**
	 * Factory method for creating info messages.
	 * 
	 * @param msg
	 *            details of the message.
	 * @return the constructed message.
	 */
	public static EngineResult message(final String msg) {
		return new EngineResult(msg, false);
	}

	/**
	 * Get the contained message.
	 * 
	 * @return whatever was passed to the factory method.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Return the error flag.
	 * 
	 * @return true iff this is an error message.
	 */
	public boolean isError() {
		return error;
	}
}
