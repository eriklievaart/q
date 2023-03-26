package com.eriklievaart.q.ui.api;

import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserContext {

	private final VirtualFile dir;
	private final List<VirtualFile> urls;

	public BrowserContext(VirtualFile dir, List<VirtualFile> urls) {
		Check.noneNull(dir, urls);
		this.dir = dir;
		this.urls = urls;
	}

	public VirtualFile getDirectory() {
		return dir;
	}

	public List<VirtualFile> getUrls() {
		return new ArrayList<>(urls);
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", dir.getUrl().getUrlUnescaped());
	}
}