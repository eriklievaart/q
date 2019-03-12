package com.eriklievaart.q.test;

import org.junit.After;
import org.junit.Before;

import com.eriklievaart.toolkit.test.api.SandboxTest;

public class AutoSandboxTest extends SandboxTest {

	@Before
	public void init() {
		createSandbox();
	}

	@After
	public void cleanup() {
		deleteSandboxFiles();
	}
}
