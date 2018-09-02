package com.eriklievaart.q.zfind.ui;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;

public class FindResultsU extends SandboxTest {

	@Test
	public void getFileMessageSingle() throws Exception {
		MemoryFile file = memoryFile("file");
		FindResults results = new FindResults(Arrays.asList(new FindResult(file, "./file")));
		String message = results.getFileMessage("dummy?");
		String expected = "<html>dummy?<br/><br/>mem:///file<br/>";
		Assertions.assertThat(message).isEqualTo(expected);
	}

	@Test
	public void getFileMessageEleven() throws Exception {
		List<FindResult> data = NewCollection.list();
		int i = 0;
		while (i++ < 11) {
			data.add(new FindResult(memoryFile("" + i), "./" + i));
		}
		FindResults results = new FindResults(data);

		String message = results.getFileMessage("blabla?");
		Assertions.assertThat(message).contains("blabla?");
		Assertions.assertThat(message).contains("10");
		Assertions.assertThat(message).contains("...");
		Assertions.assertThat(message).doesNotContain("11");
	}

	@Test
	public void getUrls() throws Exception {
		FindResult ab = new FindResult(memoryFile("a b"), "./a b");
		FindResult cd = new FindResult(memoryFile("c d"), "./c d");
		FindResults results = new FindResults(Arrays.asList(ab, cd));

		String urls = results.getUrls();
		String expected = "mem:///a%20b mem:///c%20d";
		Assertions.assertThat(urls).isEqualTo(expected);

	}
}
