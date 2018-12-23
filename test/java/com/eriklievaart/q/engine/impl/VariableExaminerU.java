package com.eriklievaart.q.engine.impl;

import java.util.List;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.SetTool;

public class VariableExaminerU {

	private VariableExaminer instance() {
		VariableExaminer instance = new VariableExaminer();
		return instance;
	}

	@Test
	public void getKeysStartingWith() {
		List<String> keys = instance().getKeysStartingWith("pre", SetTool.of("proper", "present"));

		CheckCollection.isPresent(keys, "present");
		CheckCollection.isSize(keys, 1);
	}
}
