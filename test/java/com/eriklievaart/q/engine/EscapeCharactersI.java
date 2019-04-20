package com.eriklievaart.q.engine;

import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.boot.Main;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.ui.api.QContextFactory;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class EscapeCharactersI extends SandboxTest {

	@Before
	public void init() {
		System.setProperty("q.test", "true");
	}

	@Test
	public void testOperationsWithFilesWithEscapeCharacters() throws Exception {
		DummyBeanFactory factory = Main.wireApplication();

		SystemFile dir1 = createDirectory("%A");
		SystemFile dir2 = createDirectory("%B");
		SystemFile dir3 = createDirectory("%C");
		String url1 = dir1.getUrl().getUrlEscaped();
		factory.getEngineSupplier().get().invoke(Str.sub("new -f % %", url1, UrlTool.escape("%`")));
		checkIsFile("%A/%`");

		SystemFile original = systemFile("%A/%`");
		String original1 = original.getUrl().getUrlEscaped();
		String url2 = dir2.getUrl().getUrlEscaped();
		factory.getEngineSupplier().get().invoke(Str.sub("copy -s % % %", original1, url2, UrlTool.escape("%@")));
		checkIsFile("%A/%`");
		checkIsFile("%B/%@");

		String url3 = dir3.getUrl().getUrlEscaped();
		factory.getEngineSupplier().get().invoke(Str.sub("move -s % % %", original1, url3, UrlTool.escape("%T")));
		checkNotExists("%A/%`");
		checkIsFile("%C/%T");

		SystemFile copy = systemFile("%B/%@");
		factory.getEngineSupplier().get().invoke(Str.sub("delete -u %", copy.getUrl().getUrlEscaped()));
		checkNotExists("%B/%@");
	}

	@Test
	public void testCopyUrls() throws Exception {
		DummyBeanFactory factory = Main.wireApplication();

		SystemFile with = createFile("file with space");
		SystemFile without = createFile("filewithoutspace");
		createDirectory("dir");
		String urls = with.getUrl().getUrlEscaped() + " " + without.getUrl().getUrlEscaped();

		factory.getEngineSupplier().get().invoke(Str.sub("copy -u % %", urls, urlEscaped("dir")));
		checkIsFile("dir/file with space");
		checkIsFile("dir/filewithoutspace");
		checkIsFile("file with space");
		checkIsFile("filewithoutspace");
	}

	@Test
	public void testMoveUrl() throws Exception {
		DummyBeanFactory factory = Main.wireApplication();

		SystemFile file = createFile("file with space");
		createDirectory("dir");
		String url = file.getUrl().getUrlEscaped();

		factory.getEngineSupplier().get().invoke(Str.sub("move -u % %", url, urlEscaped("dir")));
		checkIsFile("dir/file with space");
		checkNotExists("file with space");
	}

	@Test
	public void variableWithEscapeCharacters() throws Exception {
		DummyBeanFactory factory = Main.wireApplication();
		SystemFile dir1 = createDirectory("a b");
		factory.context(new QContextFactory().leftDir(dir1).make());

		// The next line will fail if $dir1 does not exist (or is not properly unescaped)
		EngineResult result = factory.getEngineSupplier().get().parse("execute -c $dir1");
		Check.isEqual(result.getMessage(), "execute -command $dir1");
	}
}
