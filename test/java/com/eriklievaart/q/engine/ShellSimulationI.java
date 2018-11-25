package com.eriklievaart.q.engine;

import java.nio.file.FileSystemException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.boot.Main;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.toolkit.test.api.SandboxTest;

public class ShellSimulationI extends SandboxTest {

	@Before
	public void init() {
		createSandbox();
		System.setProperty("q.test", "true");
	}

	@After
	public void cleanup() {
		deleteSandboxFiles();
	}

	@Test
	public void engineIntegrationScript() throws FileSystemException, InterruptedException {
		DummyBeanFactory factory = Main.wireApplication();
		Engine engine = factory.getEngineSupplier().get();

		createDirectory("");
		engine.invoke("location -lu " + quoted(""));
		engine.invoke("location -ru " + quoted(""));

		checkNotExists("dir/file");

		engine.invoke("new -f " + quoted("dir") + "`file`");
		checkIsFile("dir/file");

		engine.invoke("move -u " + quoted("dir/file") + quoted("dir2"));
		checkNotExists("dir/file");
		checkIsFile("dir2/file");
	}

}
