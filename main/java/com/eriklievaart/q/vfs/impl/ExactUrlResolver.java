package com.eriklievaart.q.vfs.impl;

import java.util.Optional;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class ExactUrlResolver {

	private MemoryFileSystem memory;

	public ExactUrlResolver(MemoryFileSystem memory) {
		this.memory = memory;
	}

	public VirtualFile resolve(String url) {
		Optional<String> option = UrlTool.getProtocol(url);

		if (option.isPresent()) {
			if (option.get().equals("mem")) {
				return memory.resolve(url);
			}
			if (!option.get().equals("file")) {
				throw new AssertionException("unknown protocol: " + option.get());
			}
		}
		return new SystemFile(UrlTool.getPath(url));
	}
}
