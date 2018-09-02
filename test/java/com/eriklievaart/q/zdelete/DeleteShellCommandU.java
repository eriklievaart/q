package com.eriklievaart.q.zdelete;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.test.api.SandboxTest;

public class DeleteShellCommandU extends SandboxTest {

	private DeleteShellCommand testable;

	@Before
	public void init() {
		createSandbox();
		testable = new DeleteShellCommand(new TrashCache());
	}

	@After
	public void cleanup() {
		deleteSandboxFiles();
	}

	@Test
	public void deleteSingleFile() throws Exception {
		createFile("directory/file");
		checkExists("directory/file");

		testable.single(systemFile("directory/file")).invoke(null);

		checkNotExists("directory/file");
		checkExists("directory");
	}

	@Test
	public void deleteSingleDir() throws Exception {
		createFile("directory/file");
		checkExists("directory/file");

		testable.single(systemFile("directory")).invoke(null);

		checkNotExists("directory/file");
		checkNotExists("directory");
	}

	@Test
	public void deleteSingleTrash() throws Exception {
		createDirectory(".Trash-1000");
		createFile("directory/deleteme");
		checkExists("directory/deleteme");
		checkExists(".Trash-1000");

		testable.single(systemFile("directory/deleteme")).invoke(null);

		checkNotExists("directory/deleteme");
		checkExists("directory");
		checkExists(".Trash-1000/files/deleteme");
	}

	@Test
	public void deleteSinglePermanent() throws Exception {
		createDirectory(".Trash-1000");
		createFile("directory/deleteme");
		checkExists("directory/deleteme");
		checkExists(".Trash-1000");

		testable.single(systemFile("directory/deleteme")).permanent().invoke(null);

		checkNotExists("directory/deleteme");
		checkExists("directory");
		checkNotExists(".Trash-1000/files/deleteme");
	}

	@Test
	public void deleteInTrash() throws Exception {
		createFile(".Trash-1000/deleteme");
		checkExists(".Trash-1000/deleteme");

		testable.single(systemFile(".Trash-1000/deleteme")).invoke(null);

		checkExists(".Trash-1000");
		checkNotExists(".Trash-1000/deleteme");
		checkNotExists(".Trash-1000/files/deleteme");
	}

	@Test
	public void deleteInTrashFiles() throws Exception {
		createFile(".Trash-1000/files/deleteme");
		checkExists(".Trash-1000/files/deleteme");

		testable.urls(Arrays.asList(systemFile(".Trash-1000/files/deleteme"))).invoke(null);

		checkExists(".Trash-1000/files");
		Check.isTrue(systemFile(".Trash-1000/files").getChildren().isEmpty());
	}

	@Test
	public void deleteUrls() throws Exception {
		createDirectory("deleteme2");
		createDirectory("keep");
		createFile("deleteme1/file");
		checkExists("deleteme1/file");
		checkExists("deleteme2");
		checkExists("keep");

		testable.urls(Arrays.asList(systemFile("deleteme1"), systemFile("deleteme2"))).invoke(null);

		checkNotExists("deleteme1/file");
		checkNotExists("deleteme2");
		checkExists("keep");
	}

}
