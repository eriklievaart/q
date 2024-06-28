package com.eriklievaart.q.vfs.api;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public interface ProtocolResolver {

	public String getProtocol();

	public VirtualFile resolve(String path);
}
