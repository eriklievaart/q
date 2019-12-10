package com.eriklievaart.q.zfind.ui;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FindResult {

	private VirtualFile file;
	private String label;

	public FindResult(VirtualFile file, String label) {
		this.file = file;
		this.label = label;
	}

	public VirtualFile getVirtualFile() {
		return file;
	}

	public String getUrlEscaped() {
		return file.getUrl().getUrlEscaped();
	}

	public String getUrlUnescaped() {
		return file.getUrl().getUrlUnescaped();
	}

	@Override
	public String toString() {
		return label;
	}
}