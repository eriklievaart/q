package com.eriklievaart.q.ui.render.browser;

import java.awt.Color;

import com.eriklievaart.q.api.render.ColorFactory;

public class VirtualFileWrapperColorFactory implements ColorFactory {

	@Override
	public Color getColor(final Object object) {
		VirtualFileWrapper file = (VirtualFileWrapper) object;
		boolean directory = file.getVirtualFile().isDirectory();
		return directory ? Color.BLUE : Color.BLACK;
	}
}
