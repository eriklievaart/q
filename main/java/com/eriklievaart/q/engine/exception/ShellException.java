package com.eriklievaart.q.engine.exception;

import com.eriklievaart.toolkit.lang.api.str.Str;

public class ShellException extends Exception {

	public ShellException(String message) {
		super(message);
	}

	public ShellException(String format, Object... args) {
		super(Str.sub(format, args));
	}
}
