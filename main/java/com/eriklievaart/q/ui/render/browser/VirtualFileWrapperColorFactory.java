package com.eriklievaart.q.ui.render.browser;

import java.awt.Color;

import com.eriklievaart.q.api.render.ColorFactory;
import com.eriklievaart.q.api.render.JListThemed;

public class VirtualFileWrapperColorFactory implements ColorFactory {

	private JListThemed<VirtualFileWrapper> fileList;

	public VirtualFileWrapperColorFactory(JListThemed<VirtualFileWrapper> fileList) {
		this.fileList = fileList;
	}

	@Override
	public Color getColor(final Object object) {
		VirtualFileWrapper file = (VirtualFileWrapper) object;
		boolean directory = file.getVirtualFile().isDirectory();
		return directory ? fileList.getForegroundDirectory() : fileList.getForegroundNormal();
	}
}