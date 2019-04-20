package com.eriklievaart.q.zrename;

import java.awt.Color;

import org.junit.Test;

import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.q.zexecute.DummyQMainUi;
import com.eriklievaart.q.zrename.ui.RenameController;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;

public class RenameControllerU extends SandboxTest {

	@Test
	public void updateRegexInvalid() {
		MemoryFile dir = memoryFile("root");
		dir.resolve("thon.py").createFile();

		RenameController controller = new RenameController(() -> new DummyQMainUi(), () -> null);
		controller.showUi(dir);
		controller.regexField.setText("\\");
		controller.regexUpdated();

		Check.isEqual(controller.regexField.getBackground(), Color.ORANGE);
		Check.isEqual(controller.fromList.getModel().getElementAt(0).getText(), "thon.py");
		Check.isEqual(controller.toList.getModel().getElementAt(0).getText(), "thon.py");
	}

	@Test
	public void updateRegexFilter() {
		MemoryFile dir = memoryFile("root");
		dir.resolve("pical.py").createFile();
		dir.resolve("son.java").createFile();
		dir.resolve("thon.py").createFile();

		RenameController controller = new RenameController(() -> new DummyQMainUi(), () -> null);
		controller.showUi(dir);
		controller.regexField.setText(".*[.]py");
		controller.regexUpdated();

		Check.isEqual(controller.fromList.getModel().getElementAt(0).getText(), "pical.py");
		Check.isEqual(controller.fromList.getModel().getElementAt(0).getForeground(), Color.BLACK);
		Check.isEqual(controller.toList.getModel().getElementAt(0).getText(), "pical.py");
		Check.isEqual(controller.toList.getModel().getElementAt(0).getForeground(), Color.BLACK);

		Check.isEqual(controller.fromList.getModel().getElementAt(1).getText(), "son.java");
		Check.isEqual(controller.fromList.getModel().getElementAt(1).getForeground(), Color.GRAY);
		Check.isEqual(controller.toList.getModel().getElementAt(1).getText(), "son.java");
		Check.isEqual(controller.toList.getModel().getElementAt(1).getForeground(), Color.GRAY);

		Check.isEqual(controller.fromList.getModel().getElementAt(2).getText(), "thon.py");
		Check.isEqual(controller.fromList.getModel().getElementAt(2).getForeground(), Color.BLACK);
		Check.isEqual(controller.toList.getModel().getElementAt(2).getText(), "thon.py");
		Check.isEqual(controller.toList.getModel().getElementAt(2).getForeground(), Color.BLACK);

		Check.isEqual(controller.regexField.getBackground(), Color.WHITE);
		Check.isEqual(controller.toList.getModel().getSize(), 3);
	}

	@Test
	public void updateRegexRename() {
		MemoryFile dir = memoryFile("root");
		dir.resolve("pical.py").createFile();
		dir.resolve("son.java").createFile();
		dir.resolve("thon.py").createFile();

		RenameController controller = new RenameController(() -> new DummyQMainUi(), () -> null);
		controller.showUi(dir);
		controller.regexField.setText(".*[.]py");
		controller.renameField.setText("py$0");
		controller.regexUpdated();

		Check.isEqual(controller.fromList.getModel().getElementAt(0).getText(), "pical.py");
		Check.isEqual(controller.fromList.getModel().getElementAt(0).getForeground(), Color.BLACK);
		Check.isEqual(controller.toList.getModel().getElementAt(0).getText(), "pypical.py");
		Check.isEqual(controller.toList.getModel().getElementAt(0).getForeground(), Color.BLACK);

		Check.isEqual(controller.fromList.getModel().getElementAt(1).getText(), "son.java");
		Check.isEqual(controller.fromList.getModel().getElementAt(1).getForeground(), Color.GRAY);
		Check.isEqual(controller.toList.getModel().getElementAt(1).getText(), "son.java");
		Check.isEqual(controller.toList.getModel().getElementAt(1).getForeground(), Color.GRAY);

		Check.isEqual(controller.fromList.getModel().getElementAt(2).getText(), "thon.py");
		Check.isEqual(controller.fromList.getModel().getElementAt(2).getForeground(), Color.BLACK);
		Check.isEqual(controller.toList.getModel().getElementAt(2).getText(), "python.py");
		Check.isEqual(controller.toList.getModel().getElementAt(2).getForeground(), Color.BLACK);

		Check.isEqual(controller.regexField.getBackground(), Color.WHITE);
		Check.isEqual(controller.toList.getModel().getSize(), 3);
	}
}
