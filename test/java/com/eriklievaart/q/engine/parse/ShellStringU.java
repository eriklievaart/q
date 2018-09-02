package com.eriklievaart.q.engine.parse;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class ShellStringU {

	@Test
	public void escapeString() {
		Check.isEqual(ShellString.escape("need \\:|-@$` escapes"), "need \\b:|-@$\\q escapes");
	}

	@Test
	public void unescapeString() {
		Check.isEqual(ShellString.unescape("need \\b\\q escapes"), "need \\` escapes");
	}

	@Test
	public void unescapeStringBackslashBorderCaseSlash() {
		Check.isEqual(ShellString.unescape("\\b:|"), "\\:|");
	}
}
