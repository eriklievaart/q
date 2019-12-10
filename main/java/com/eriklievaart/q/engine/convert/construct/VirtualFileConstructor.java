package com.eriklievaart.q.engine.convert.construct;

import java.util.function.Supplier;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.construct.AbstractConstructor;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class VirtualFileConstructor extends AbstractConstructor<VirtualFile> {

	private Supplier<UrlResolver> resolver;

	public VirtualFileConstructor(Supplier<UrlResolver> resolver) {
		this.resolver = resolver;
	}

	@Override
	public VirtualFile constructObject(final String str) {
		return resolver.get().resolve(str);
	}
}