package com.eriklievaart.q.vfs.protocol;

import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FileProtocolResolver implements ProtocolResolver {

	@Override
	public String getProtocol() {
		return "file";
	}

	@Override
	public VirtualFile resolve(String path) {
		return new SystemFile(UrlTool.getPath(path));
	}
}
