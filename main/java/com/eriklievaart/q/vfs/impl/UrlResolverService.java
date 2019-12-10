package com.eriklievaart.q.vfs.impl;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class UrlResolverService implements UrlResolver {

	private MemoryFileSystem memory = new MemoryFileSystem();

	@Override
	public VirtualFile resolve(String url) {
		return new ExactUrlResolver(memory).resolve(url);
	}

	@Override
	public VirtualFile resolveFuzzy(VirtualFile base, String location) {
		return new FuzzyUrlResolver(new ExactUrlResolver(memory)).resolve(base, location);
	}
}