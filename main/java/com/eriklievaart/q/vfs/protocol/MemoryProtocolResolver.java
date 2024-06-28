package com.eriklievaart.q.vfs.protocol;

import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class MemoryProtocolResolver implements ProtocolResolver {

	private MemoryFileSystem memory = new MemoryFileSystem();

	@Override
	public String getProtocol() {
		return "mem";
	}

	@Override
	public VirtualFile resolve(String path) {
		Check.matches(path, "(mem://)?([a-zA-Z]:)?[/\\\\].*+");
		return memory.resolve(path);
	}
}
