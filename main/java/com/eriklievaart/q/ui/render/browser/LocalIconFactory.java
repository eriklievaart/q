package com.eriklievaart.q.ui.render.browser;

import java.util.Map;

import javax.swing.Icon;

import com.eriklievaart.q.ui.render.list.IconFactory;

public class LocalIconFactory implements IconFactory {

	private final Map<String, Icon> cache;
	private final Icon dir;

	public LocalIconFactory(final LocalIconLoader loader) {
		dir = loader.getDirIcon();
		cache = loader.getMapping();
	}

	@Override
	public Icon getIcon(final Object object) {
		return getIcon((VirtualFileWrapper) object);
	}

	private Icon getIcon(final VirtualFileWrapper file) {
		if (file.getVirtualFile().isDirectory()) {
			return dir;
		}
		Icon cached = cache.get(file.getVirtualFile().getUrl().getExtension().toLowerCase());
		return cached != null ? cached : cache.get("bin");
	}

}
