package com.eriklievaart.q.engine.convert;

import java.util.Arrays;

import org.junit.Test;

import com.eriklievaart.q.engine.convert.construct.ListConstructor;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ListConstructorU {

	@Test
	public void constructObjectEmpty() {
		Check.isEqual(new ListConstructor(':').constructObject(""), Arrays.asList(""));
	}

	@Test
	public void constructObjectSingle() {
		Check.isEqual(new ListConstructor(':').constructObject("a"), Arrays.asList("a"));
	}

	@Test
	public void constructObjectDouble() {
		Check.isEqual(new ListConstructor(':').constructObject("a:b"), Arrays.asList("a", "b"));
	}

	@Test
	public void constructObjectJustSplitter() {
		Check.isEqual(new ListConstructor(':').constructObject(":"), Arrays.asList("", ""));
	}


}
