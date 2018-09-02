package com.eriklievaart.q.zfind;

import javax.swing.DefaultListModel;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.api.engine.DummyPluginContext;
import com.eriklievaart.q.zfind.ui.FindController;
import com.eriklievaart.q.zfind.ui.FindResult;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.test.api.BombSquad;
import com.eriklievaart.toolkit.test.api.SandboxTest;

public class FindShellCommandU extends SandboxTest {

	@Test
	public void findFiles() throws Exception {
		memoryFile("root/dir").mkdir();
		memoryFile("root/nested/file").createFile();

		FindController controller = new FindController(() -> null, () -> null);
		new FindShellCommand(controller).root(memoryFile("root")).type("FILE").invoke(new DummyPluginContext());
		Thread.sleep(1); // Wait for Swing thread to take results from the queue

		DefaultListModel<FindResult> model = controller.getModel();
		Assertions.assertThat(model.get(0).toString()).isEqualTo("./nested/file");
		Assertions.assertThat(model.getSize()).isEqualTo(1);
	}

	@Test
	public void validateType() {
		FindController controller = new FindController(() -> null, () -> null);
		BombSquad.diffuse(AssertionException.class, "FILE|DIRECTORY", () -> {
			new FindShellCommand(controller).type("mispoes").validate(new DummyPluginContext());
		});
	}
}
