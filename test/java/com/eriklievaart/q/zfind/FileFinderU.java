package com.eriklievaart.q.zfind;

import java.util.Iterator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FileFinderU extends SandboxTest {

	@Test
	public void findFiles() throws Exception {
		memoryFile("root/dir").mkdir();
		memoryFile("root/file").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).filesOnly().scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/file"));
	}

	@Test
	public void findDirectories() throws Exception {
		memoryFile("root/dir").mkdir();
		memoryFile("root/file").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).directoriesOnly().scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/dir"));
	}

	@Test
	public void findOrdering() throws Exception {
		MemoryFile a1 = memoryFile("root/a/a1");
		MemoryFile a2 = memoryFile("root/a/n/a2");
		MemoryFile b1 = memoryFile("root/b/b1");
		MemoryFile b2 = memoryFile("root/b/b2");

		a1.createFile();
		a2.createFile();
		b1.createFile();
		b2.createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).filesOnly().scan();
		Assertions.assertThat(iterator).containsExactly(a1, a2, b1, b2);
	}

	@Test
	public void findLocalTrue() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/dir/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).filesOnly().local(true).scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aaaa"));
	}

	@Test
	public void findLocalFalse() throws Exception {
		memoryFile("root/dir/aaaa").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).filesOnly().local(false).scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/bbbb"), memoryFile("root/dir/aaaa"));
	}

	@Test
	public void findInclude() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/aabb").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).include("aa*").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aaaa"), memoryFile("root/aabb"));
	}

	@Test
	public void findIncludeInsensitive() throws Exception {
		memoryFile("root/aAaA").createFile();
		memoryFile("root/aAbB").createFile();
		memoryFile("root/bBbB").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).include("aa*").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aAaA"), memoryFile("root/aAbB"));
	}

	@Test
	public void findIncludeMultiple() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/aabb").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).include(" aaaa | bbbb ").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aaaa"), memoryFile("root/bbbb"));
	}

	@Test
	public void findExclude() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/aabb").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).exclude("aa*").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/bbbb"));
	}

	@Test
	public void findExcludeInsensitive() throws Exception {
		memoryFile("root/aAaA").createFile();
		memoryFile("root/aAbB").createFile();
		memoryFile("root/bBbB").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).exclude("aa*").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/bBbB"));
	}

	@Test
	public void findExcludeMultiple() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/aabb").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).exclude(" aaaa | bbbb ").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aabb"));
	}

	@Test
	public void findRegexName() throws Exception {
		memoryFile("root/aaaa").createFile();
		memoryFile("root/aabb").createFile();
		memoryFile("root/bbbb").createFile();

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).regexName("a++b*+").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aaaa"), memoryFile("root/aabb"));
	}

	@Test
	public void findRegexPath() throws Exception {
		memoryFile("root/aa/aa").createFile();
		memoryFile("root/aa/bb").createFile();
		memoryFile("root/bb/bb").createFile();

		MemoryFile dir = memoryFile("root");
		Iterator<VirtualFile> iterator = new FileFinder(dir).filesOnly().regexPath(".*/a++/[^/]++").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/aa/aa"), memoryFile("root/aa/bb"));
	}

	@Test
	public void containsText() throws Exception {
		memoryFile("root/a").getContent().writeString("apple");
		memoryFile("root/b").getContent().writeString("banana");

		Iterator<VirtualFile> iterator = new FileFinder(memoryFile("root")).containsText("banana").scan();
		Assertions.assertThat(iterator).containsExactly(memoryFile("root/b"));
	}
}
