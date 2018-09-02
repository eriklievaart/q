package com.eriklievaart.q.zexecute;

public class TerminalLine {

	private final boolean error;
	private final String text;

	private TerminalLine(String line, boolean error) {
		this.text = line;
		this.error = error;
	}

	public static TerminalLine normal(String line) {
		return new TerminalLine(line, false);
	}

	public static TerminalLine error(String line) {
		return new TerminalLine(line, true);
	}

	public boolean isError() {
		return error;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}
}
