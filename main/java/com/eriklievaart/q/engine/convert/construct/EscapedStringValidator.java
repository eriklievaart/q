package com.eriklievaart.q.engine.convert.construct;

import com.eriklievaart.q.engine.parse.ShellString;
import com.eriklievaart.toolkit.convert.api.Validator;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class EscapedStringValidator implements Validator {

	@Override
	public void check(final String value) {
		Check.isTrue(isValid(value), "Not valid: %", value);
	}

	@Override
	public boolean isValid(final String value) {
		return !ShellString.hasInvalidEscapeSequence(value);
	}

}
