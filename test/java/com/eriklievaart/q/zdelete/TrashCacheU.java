package com.eriklievaart.q.zdelete;

import java.nio.file.FileSystemException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TrashCacheU extends SandboxTest {

	@Before
	public void init() {
		createSandbox();
	}

	@After
	public void cleanup() {
		deleteSandboxFiles();
	}

	@Test
	public void getTrashLocationAvailable() throws FileSystemException {
		createFile("file");
		createDirectory(".Trash-1000");

		VirtualFile found = new TrashCache().getTrashLocation(systemFile("file")).get();
		Check.isEqual(found.getPath(), systemFile(".Trash-1000/files").getPath());
	}

	@Test
	public void getTrashLocationUnavailable() throws FileSystemException {
		createFile("file");
		checkNotExists(".Trash-1000");

		Check.isFalse(new TrashCache().getTrashLocation(systemFile("file")).isPresent());
	}
}
