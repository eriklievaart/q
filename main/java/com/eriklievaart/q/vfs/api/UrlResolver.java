package com.eriklievaart.q.vfs.api;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public interface UrlResolver {

	public VirtualFile resolve(String url);

	public VirtualFile resolveFuzzy(VirtualFile base, String location);
}