package com.eriklievaart.q.znew;

import org.junit.Test;

import com.eriklievaart.q.api.engine.DummyPluginContext;
import com.eriklievaart.toolkit.mock.SandboxTest;

public class NewShellCommandU extends SandboxTest {

	@Test
	public void createFile() throws Exception {
		new NewShellCommand().file(systemFile("parent"), "child").invoke(new DummyPluginContext());
		checkIsFile("parent/child");
	}

	@Test
	public void createFileWithContents() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.setPipedContents("bla\nbla");

		new NewShellCommand().file(systemFile("parent"), "child").invoke(context);
		checkIsFile("parent/child", "bla\nbla");
	}

	@Test
	public void createDirectory() throws Exception {
		new NewShellCommand().directory(systemFile("parent"), "dir").invoke(new DummyPluginContext());
		checkIsDirectory("parent/dir");
	}
}