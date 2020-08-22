package com.eriklievaart.q.zrename.ui;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class RenameListElement {

	private VirtualFile file;
	private String text;
	private boolean active = true;

	public RenameListElement(VirtualFile file) {
		this.file = file;
		this.text = file.getName();
	}

	public VirtualFile getVirtualFile() {
		return file;
	}

	public void setActive(boolean value) {
		active = value;
	}

	public boolean isActive() {
		return active;
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		text = value;
	}

	@Override
	public String toString() {
		return text;
	}
}