package com.eriklievaart.q.zzip;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.test.DummyServiceCollection;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.mock.SandboxTest;

public class ZipShellCommandU extends SandboxTest {

	@Test
	public void zipSingle() throws Exception {
		systemFile("dir/file1.txt").getContent().writeString("1");
		systemFile("dir/file2.txt").getContent().writeString("blabla");
		systemFile("dir/nested/file3.txt").getContent().writeString("3");

		checkNotExists("dir.zip");
		testable().single(systemFile("dir"), systemFile("")).invoke(null);
		checkExists("dir.zip");

		try (ZipInputStream is = new ZipInputStream(new FileInputStream(file("dir.zip")))) {
			Map<String, String> files = NewCollection.map();
			for (int i = 0; i < 3; i++) {
				String name = is.getNextEntry().getName();
				String data = readEntry(is);
				files.put(name, data);
			}
			List<String> names = ListTool.sortedCopy(files.keySet());

			Assertions.assertThat(names).containsExactly("file1.txt", "file2.txt", "nested/file3.txt");
			Check.isEqual(files.get("file1.txt"), "1");
			Check.isEqual(files.get("file2.txt"), "blabla");
			Check.isEqual(files.get("nested/file3.txt"), "3");
			Check.isNull(is.getNextEntry());
		}
	}

	@Test
	public void unpack() throws Exception {
		systemFile("dir/nested/file.txt").getContent().writeString("blabla");
		testable().single(systemFile("dir"), systemFile("")).invoke(null);

		systemFile("destination").mkdir();
		testable().unpack(systemFile("dir.zip"), systemFile("destination")).invoke(null);
		checkIsFile("destination/nested/file.txt", "blabla");
	}

	private ZipShellCommand testable() {
		return new ZipShellCommand(new ZipController(new DummyServiceCollection<>()));
	}

	@SuppressWarnings("resource")
	private String readEntry(ZipInputStream is) {
		Scanner scanner = new Scanner(is);
		scanner.useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}
}