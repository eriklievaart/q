package com.eriklievaart.q.zcopy;

import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.mock.Bomb;
import com.eriklievaart.toolkit.mock.BombSquad;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class CopyShellCommandU extends SandboxTest {

	private static final String ORIGINAL_NAME = "";
	private CopyShellCommand testable;

	@Before
	public void init() {
		testable = new CopyShellCommand();
	}

	@Test
	public void copySingleFile() throws Exception {
		createFile("source/file");
		checkNotExists("destination/file");

		testable.single(systemFile("source/file"), systemFile("destination"), ORIGINAL_NAME).invoke(null);

		checkIsFile("source/file");
		checkIsFile("destination/file");
	}

	@Test
	public void copySingleFileWithExtension() throws Exception {
		createFile("source/file.txt");
		checkNotExists("destination/file.txt");

		testable.single(systemFile("source/file.txt"), systemFile("destination"), ORIGINAL_NAME).invoke(null);

		checkIsFile("source/file.txt");
		checkIsFile("destination/file.txt");
	}

	@Test
	public void copySingleFileTimestamp() throws Exception {
		SystemFile parentDir = systemFile("source");
		SystemFile sourceFile = systemFile("source/file");

		sourceFile.createFile();
		Assertions.assertThat(parentDir.getChildren()).hasSize(1);

		testable.single(sourceFile, parentDir, ORIGINAL_NAME).invoke(null);
		checkIsFile("source/file");

		List<VirtualFile> children = parentDir.getChildren();
		CheckCollection.isSize(children, 2);
		children.remove(sourceFile);
		CheckCollection.isSize(children, 1);

		String created = children.get(0).getName();
		Check.matches(created, "file-\\d++");
	}

	@Test
	public void copySingleFileReplaceTimestamp() throws Exception {
		SystemFile parentDir = systemFile("source");
		SystemFile sourceFile = systemFile("source/file-1707213002754.txt");

		sourceFile.createFile();
		Assertions.assertThat(parentDir.getChildren()).hasSize(1);

		testable.single(sourceFile, parentDir, ORIGINAL_NAME).invoke(null);
		checkIsFile("source/file-1707213002754.txt");

		List<VirtualFile> children = parentDir.getChildren();
		CheckCollection.isSize(children, 2);
		children.remove(sourceFile);
		CheckCollection.isSize(children, 1);

		String created = children.get(0).getName();
		Check.matches(created, "file-\\d++.txt");
	}

	@Test
	public void copySingleFileWithExtensionTimestamp() throws Exception {
		SystemFile parentDir = systemFile("source");
		SystemFile sourceFile = systemFile("source/file.txt");

		sourceFile.createFile();
		CheckCollection.isSize(parentDir.getChildren(), 1);

		testable.single(sourceFile, parentDir, ORIGINAL_NAME).invoke(null);
		checkIsFile("source/file.txt");

		List<VirtualFile> children = parentDir.getChildren();
		CheckCollection.isSize(children, 2);
		children.remove(sourceFile);
		CheckCollection.isSize(children, 1);

		String created = children.get(0).getName();
		Check.matches(created, "file-\\d++.txt");
	}

	@Test
	public void copySingleFileRename() throws Exception {
		SystemFile sourceFile = systemFile("source/file");

		sourceFile.createFile();
		checkNotExists("destination/renamed");

		testable.single(sourceFile, systemFile("destination"), "renamed").invoke(null);

		checkIsFile("source/file");
		checkIsFile("destination/renamed");
	}

	@Test
	public void copySingleDirectory() throws Exception {
		SystemFile sourceDir = systemFile("source/dir");

		sourceDir.mkdir();
		checkNotExists("destination/renamed");

		testable.single(sourceDir, systemFile("destination"), "renamed").invoke(null);

		checkIsEmptyDirectory("destination/renamed");
		checkExists("source/dir");
	}

	@Test
	public void copySingleDirectoryWithChild() throws Exception {
		SystemFile sourceDir = systemFile("source/dir");
		SystemFile sourceFile = systemFile("source/dir/file");
		String destination = "destination";

		sourceFile.createFile();
		checkNotExists(destination);

		testable.single(sourceDir, systemFile(destination), ORIGINAL_NAME).invoke(null);

		checkIsDirectoryWithChildren(destination);
		checkExists("source/dir");
	}

	@Test
	public void copySingleFileRenameSameName() throws Exception {
		createFile("source/file");

		testable.single(systemFile("source/file"), systemFile("source"), "file").invoke(null);

		checkIsFile("source/file");
	}

	@Test
	public void copySingleFileToItsOwnChild() throws Exception {
		createDirectory("parent/child");

		BombSquad.diffuse(AssertionException.class, "child of itself", new Bomb() {
			@Override
			public void explode() throws Exception {
				testable.single(systemFile("parent"), systemFile("parent/child"), "parent").invoke(null);
			}
		});

		checkIsDirectory("parent");
		checkIsEmptyDirectory("parent/child");
	}

	@Test
	public void copySingleFileToUnrelatedParent() throws Exception {
		createDirectory("dir");
		createDirectory("dir3");

		testable.single(systemFile("dir"), systemFile("dir3"), "rename").invoke(null);

		checkExists("dir");
		checkExists("dir3/rename");
	}

	@Test
	public void copyUrls() {
		createFile("source/file");
		createDirectory("source/dir");
		createDirectory("destination");

		checkNotExists("destination/file");
		checkNotExists("destination/dir");

		List<VirtualFile> urls = Arrays.asList(systemFile("source/file"), systemFile("source/dir"));
		testable.urls(urls, systemFile("destination")).invoke(null);

		checkExists("destination/file");
		checkExists("destination/dir");
		checkExists("source/file");
		checkExists("source/dir");
	}

	@Test(expected = PluginException.class)
	public void copyUrlsDuplicateName() throws PluginException {
		List<VirtualFile> urls = Arrays.asList(systemFile("source1/file"), systemFile("source2/file"));
		testable.urls(urls, systemFile("destination")).validate(null);
	}

	@Test
	public void copyUrlsUniqueName() throws PluginException {
		List<VirtualFile> urls = Arrays.asList(systemFile("source1/file"), systemFile("source1/other"));
		testable.urls(urls, systemFile("destination")).validate(null);
	}

	@Test
	public void copyUrlSingleTimestamp() throws Exception {
		SystemFile parentDir = systemFile("source");
		SystemFile sourceFile = systemFile("source/file");

		sourceFile.createFile();
		Assertions.assertThat(parentDir.getChildren()).hasSize(1);

		testable.urls(Arrays.asList(sourceFile), parentDir).invoke(null);
		checkIsFile("source/file");

		List<VirtualFile> children = parentDir.getChildren();
		CheckCollection.isSize(children, 2);
		children.remove(sourceFile);
		CheckCollection.isSize(children, 1);

		String created = children.get(0).getName();
		Check.matches(created, "file-\\d++");
	}

	@Test
	public void copyUrlsMergeContents() throws FileSystemException {
		createFile("source/dir/add");
		createFile("destination/dir/original");

		checkNotExists("destination/dir/file1");

		List<VirtualFile> urls = Arrays.asList(systemFile("source/dir"));
		testable.urls(urls, systemFile("destination")).invoke(null);

		checkIsFile("destination/dir/add");
		checkIsFile("destination/dir/original");
		checkIsDirectoryWithChildren("source/dir");
	}

	@Test
	public void copyIncludes() {
		createFile("source/file.java");
		createFile("source/file.py");
		createDirectory("destination");

		checkNotExists("destination/file.java");
		checkNotExists("destination/file.py");

		testable.include("*.java", systemFile("source"), systemFile("destination")).invoke(null);

		checkExists("source/file.py");
		checkExists("source/file.java");
		checkExists("destination/file.java");
		checkNotExists("destination/file.py");
	}

	@Test
	public void copyIncludesNested() throws Exception {
		createFile("source/dir/a.java");
		createFile("source/dir/b.py");
		checkNotExists("destination");

		testable.include("*.py", systemFile("source"), systemFile("destination")).invoke(null);

		checkExists("source/dir/a.java");
		checkExists("source/dir/b.py");
		checkExists("destination/dir/b.py");
		checkNotExists("destination/dir/a.java");
	}
}