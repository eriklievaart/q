package com.eriklievaart.q.zmove;

import java.nio.file.FileSystemException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.test.api.Bomb;
import com.eriklievaart.toolkit.test.api.BombSquad;
import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class MoveShellCommandU extends SandboxTest {

	private static final String ORIGINAL_NAME = "";
	private MoveShellCommand testable;

	@Before
	public void init() {
		createSandbox();
		testable = new MoveShellCommand();
	}

	@After
	public void cleanup() {
		deleteSandboxFiles();
	}

	@Test
	public void moveSingleFile() throws Exception {
		createFile("source/file");
		checkNotExists("destination/file");

		testable.single(systemFile("source/file"), systemFile("destination"), ORIGINAL_NAME).invoke(null);

		checkNotExists("source/file");
		checkIsFile("destination/file");
	}

	@Test
	public void moveSingleFileWithExtension() throws Exception {
		createFile("source/file.txt");
		checkNotExists("destination/file.txt");

		testable.single(systemFile("source/file.txt"), systemFile("destination"), ORIGINAL_NAME).invoke(null);

		checkNotExists("source/file.txt");
		checkIsFile("destination/file.txt");
	}

	@Test
	public void moveSingleFileRename() throws Exception {
		SystemFile sourceFile = systemFile("source/file");

		sourceFile.createFile();
		checkNotExists("destination/renamed");

		testable.single(sourceFile, systemFile("destination"), "renamed").invoke(null);

		checkNotExists("source/file");
		checkIsFile("destination/renamed");
	}

	@Test
	public void moveSingleFileRenameTrim() throws Exception {
		SystemFile sourceFile = systemFile("source/file");

		sourceFile.createFile();
		checkNotExists("destination/renamed");

		testable.single(sourceFile, systemFile("destination"), " renamed ").invoke(null);

		checkNotExists("source/file");
		checkIsFile("destination/renamed");
	}

	@Test
	public void moveSingleDirectory() throws Exception {
		SystemFile sourceDir = systemFile("source/dir");

		sourceDir.mkdir();
		checkNotExists("destination/renamed");

		testable.single(sourceDir, systemFile("destination"), "renamed").invoke(null);

		checkNotExists("source/dir");
		checkIsEmptyDirectory("destination/renamed");
	}

	@Test
	public void moveSingleDirectoryWithChild() throws Exception {
		SystemFile sourceDir = systemFile("source/dir");
		SystemFile sourceFile = systemFile("source/dir/file");
		String destination = "destination";

		sourceFile.createFile();
		checkNotExists(destination);

		testable.single(sourceDir, systemFile(destination), ORIGINAL_NAME).invoke(null);

		checkIsDirectoryWithChildren(destination);
		checkNotExists("source/dir");
	}

	@Test
	public void moveSingleFileRenameSameName() throws Exception {
		createFile("source/file");

		testable.single(systemFile("source/file"), systemFile("source"), "file").invoke(null);

		checkIsFile("source/file");
	}

	@Test
	public void moveSingleFileToItsOwnChild() throws Exception {
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
	public void moveSingleFileToUnrelatedParent() throws Exception {
		createDirectory("dir");
		createDirectory("dir3");

		testable.single(systemFile("dir"), systemFile("dir3"), "rename").invoke(null);

		checkNotExists("dir");
		checkExists("dir3/rename");
	}

	@Test
	public void moveUrls() {
		createFile("source/file");
		createDirectory("source/dir");
		createDirectory("destination");

		checkNotExists("destination/file");
		checkNotExists("destination/dir");

		List<VirtualFile> urls = Arrays.asList(systemFile("source/file"), systemFile("source/dir"));
		testable.urls(urls, systemFile("destination")).invoke(null);

		checkNotExists("source/file");
		checkNotExists("source/dir");
		checkExists("destination/file");
		checkExists("destination/dir");
	}

	@Test(expected = PluginException.class)
	public void moveUrlsDuplicateName() throws PluginException {
		List<VirtualFile> urls = Arrays.asList(systemFile("source1/file"), systemFile("source2/file"));
		testable.urls(urls, systemFile("destination")).validate(null);
	}

	@Test
	public void moveUrlsUniqueName() throws PluginException {
		List<VirtualFile> urls = Arrays.asList(systemFile("source1/file"), systemFile("source1/other"));
		testable.urls(urls, systemFile("destination")).validate(null);
	}

	@Test
	public void moveUrlsMergeContents() throws FileSystemException {
		createFile("source/dir/add");
		createFile("destination/dir/original");

		checkNotExists("destination/dir/file1");

		List<VirtualFile> urls = Arrays.asList(systemFile("source/dir"));
		testable.urls(urls, systemFile("destination")).invoke(null);

		checkIsFile("destination/dir/add");
		checkIsFile("destination/dir/original");
		checkNotExists("source/dir");
	}

	@Test
	public void moveIncludes() {
		createFile("source/file.java");
		createFile("source/file.py");
		createDirectory("destination");

		checkNotExists("destination/file.java");
		checkNotExists("destination/file.py");

		testable.include("*.java", systemFile("source"), systemFile("destination")).invoke(null);

		checkExists("source/file.py");
		checkNotExists("destination/file.py");
		checkNotExists("source/file.java");
		checkExists("destination/file.java");
	}

	@Test
	public void moveIncludesNested() throws Exception {
		createFile("source/dir/a.java");
		createFile("source/dir/b.py");
		checkNotExists("destination");

		testable.include("*.py", systemFile("source"), systemFile("destination")).invoke(null);

		checkExists("source/dir/a.java");
		checkNotExists("source/dir/b.py");
		checkExists("destination/dir/b.py");
		checkNotExists("destination/dir/a.java");
	}
}
