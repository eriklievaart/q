package com.eriklievaart.q.vfs.impl;

import org.junit.Test;

import com.eriklievaart.q.vfs.protocol.FileProtocolResolver;
import com.eriklievaart.q.vfs.protocol.MemoryProtocolResolver;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;
import com.eriklievaart.toolkit.mock.BombSquad;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class UrlResolverServiceU {

	@Test
	public void resolve() throws Exception {
		UrlResolverService service = new UrlResolverService();
		service.register(new MemoryProtocolResolver());
		service.register(new FileProtocolResolver());

		VirtualFile a = service.resolve("mem:///ram/a");
		Check.isInstance(MemoryFile.class, a);
		CheckStr.isEqual(a.getPath(), "/ram/a");

		VirtualFile b = service.resolve("file:///tmp/b");
		Check.isInstance(SystemFile.class, b);
		CheckStr.isEqual(b.getPath(), "/tmp/b");
	}

	@Test
	public void resolveFallback() throws Exception {
		UrlResolverService service = new UrlResolverService();
		service.register(new MemoryProtocolResolver());

		VirtualFile b = service.resolve("/tmp/b");
		Check.isInstance(SystemFile.class, b);
		CheckStr.isEqual(b.getPath(), "/tmp/b");
	}

	@Test
	public void resolveInvalidProtocol() throws Exception {
		UrlResolverService service = new UrlResolverService();
		service.register(new MemoryProtocolResolver());

		BombSquad.diffuse(AssertionException.class, "unknown protocol", () -> {
			service.resolve("bla:///tmp/b");
		});
	}
}