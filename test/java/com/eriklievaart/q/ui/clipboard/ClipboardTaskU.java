package com.eriklievaart.q.ui.clipboard;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.mock.SandboxTest;

public class ClipboardTaskU extends SandboxTest {

	@Test
	public void getFilePaths() {
		String file1 = UrlTool.getPath(url("file1"));
		String file2 = UrlTool.getPath(url("file2"));

		String paths = ClipboardTask.getFilePaths(Arrays.asList(new File(file1), new File(file2)));
		Check.isEqual(paths, file1 + "\n" + file2);
	}

	@Test
	public void unescapeAndJoinLines() {
		String file1 = url("file%201");
		String file2 = url("file%202");

		String paths = ClipboardTask.unescapeAndJoinLines(Arrays.asList(file1, file2));
		Check.isEqual(paths, file1.replace("%20", " ") + "\n" + file2.replace("%20", " "));
	}

	@Test
	public void getFiles() {
		String file1 = url("file1");
		String file2 = url("file2");

		List<File> files = new ClipboardTask("copy", file1 + "\n" + file2).getFiles();
		Check.isEqual(UrlTool.getPath(file1), files.get(0).getAbsolutePath());
		Check.isEqual(UrlTool.getPath(file2), files.get(1).getAbsolutePath());
	}

	@Test
	public void getEscapedUrlList() {
		String file1 = url("file 1");
		String file2 = url("file 2");

		List<String> files = new ClipboardTask("copy", file1 + "\n" + file2).getEscapedUrlList();
		Check.isEqual(file1.replace(" ", "%20"), files.get(0));
		Check.isEqual(file2.replace(" ", "%20"), files.get(1));
	}
}