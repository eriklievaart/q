package com.eriklievaart.q.zdelete;

import java.nio.file.FileSystemException;

import org.junit.Test;

import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TrashCacheU extends SandboxTest {

	@Test
	public void getTrashLocationAvailable() throws FileSystemException {
		createFile("file");
		createDirectory(".Trash-1000");

		VirtualFile found = new TrashCache().getTrashLocation(systemFile("file")).get();
		Check.isEqual(found.getPath(), systemFile(".Trash-1000/files").getPath());
	}

	@Test
	public void getTrashLocationHome() throws FileSystemException {
		SystemFile home = createDirectory("home");
		SystemFile file = createFile("home/file");
		createDirectory("home/.local/share/Trash");

		TrashCache testable = new TrashCache();
		testable.home = () -> home.getPath();

		VirtualFile found = testable.getTrashLocation(file).get();
		Check.isEqual(found.getPath(), systemFile("home/.local/share/Trash").getPath());
	}

	@Test
	public void getTrashLocationAvailableFromCache() throws FileSystemException {
		TrashCache cache = new TrashCache();

		createFile("dir/nested/c/a");
		createFile("dir/nested/c/b");
		createDirectory(".Trash-1000");

		VirtualFile trashA = cache.getTrashLocation(systemFile("dir/nested/c/a")).get();
		Check.isEqual(trashA.getPath(), systemFile(".Trash-1000/files").getPath());

		VirtualFile trashB = cache.getTrashLocation(systemFile("dir/nested/c/b")).get();
		Check.isEqual(trashB.getPath(), systemFile(".Trash-1000/files").getPath());
	}

	@Test
	public void getTrashLocationUnavailable() throws FileSystemException {
		createFile("file");
		checkNotExists(".Trash-1000");

		Check.isFalse(new TrashCache().getTrashLocation(systemFile("file")).isPresent());
	}
}