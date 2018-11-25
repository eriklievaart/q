package com.eriklievaart.q.zrename.ui;

import java.awt.Color;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class RenameListElement {

	private VirtualFile file;
	private Color foreground;
	private String text;

	public RenameListElement(VirtualFile file) {
		this.file = file;
		this.text = file.getName();
	}

	public VirtualFile getVirtualFile() {
		return file;
	}

	public void setForeground(Color color) {
		this.foreground = color;
	}

	public Color getForeground() {
		return foreground;
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
