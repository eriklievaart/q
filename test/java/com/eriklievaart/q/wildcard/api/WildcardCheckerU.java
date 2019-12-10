package com.eriklievaart.q.wildcard.api;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class WildcardCheckerU {

	@Test
	public void specificPass() {
		Check.isTrue(new WildcardChecker("gobblediegook").matches("gobblediegook"));
	}

	@Test
	public void specificFail() {
		Check.isFalse(new WildcardChecker("hocuspocus").matches("gobblediegook"));
	}

	@Test
	public void wildcardPass() {
		Check.isTrue(new WildcardChecker("*gook").matches("gobblediegook"));
	}

	@Test
	public void wildcardFail() {
		Check.isFalse(new WildcardChecker("*pocus").matches("gobblediegook"));
	}

	@Test
	public void wildcardWhitespacePass() {
		Check.isTrue(new WildcardChecker(" *die gook ").matches("gobbledie gook"));
	}

	@Test
	public void wildcardWhitespaceFail() {
		Check.isFalse(new WildcardChecker("*die gook").matches("gobblediegook"));
	}

	@Test
	public void wildcardMultiplePass() {
		Check.isTrue(new WildcardChecker(" *.java, *.py ").matches("bla.py"));
	}

	@Test
	public void wildcardMultipleFail() {
		Check.isFalse(new WildcardChecker(" *.java, *.py ").matches("bla.sh"));
	}
}