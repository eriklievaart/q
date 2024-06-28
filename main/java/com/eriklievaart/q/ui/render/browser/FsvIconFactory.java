package com.eriklievaart.q.ui.render.browser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.metal.MetalIconFactory;

import com.eriklievaart.q.api.render.IconFactory;
import com.eriklievaart.q.api.render.VirtualFileWrapper;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class FsvIconFactory implements IconFactory {
	private static final boolean LINUX = System.getProperty("os.name").equalsIgnoreCase("linux");
	private static final Icon FILE_ICON = MetalIconFactory.getTreeLeafIcon();
	private static final Icon DIR_ICON = MetalIconFactory.getTreeFolderIcon();

	private static final LogTemplate log = new LogTemplate(FsvIconFactory.class);
	private static final Map<String, Icon> CACHE = NewCollection.weakMap();

	private Icon directory;

	static {
		if (LINUX) {
			log.info("Native Icons unavailable under Linux, returning default icons.");
		}
	}

	@Override
	public Icon getIcon(final Object file) {
		return getIcon((VirtualFileWrapper) file);
	}

	public Icon getIcon(final VirtualFileWrapper file) {
		return file.getVirtualFile().isDirectory() ? getDirectoryIcon() : getSystemIcon(file);
	}

	private Icon getSystemIcon(final VirtualFileWrapper file) {
		String ext = file.getVirtualFile().getUrl().getExtension();
		if (!CACHE.containsKey(ext) || CACHE.get(ext) == null) {
			try {
				CACHE.put(ext, loadSystemIcon(file));
			} catch (IOException e) {
				log.warn("Unable to read system icon for: " + file.getVirtualFile(), e);
				return null;
			}
		}
		return CACHE.get(ext);
	}

	private Icon getDirectoryIcon() {
		if (directory == null) {
			directory = loadFolderIcon();
		}
		return directory;
	}

	private Icon loadFolderIcon() {
		if (LINUX) {
			return DIR_ICON;
		}
		Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File("c:/Users"));
		log.debug("directory icon: " + icon);
		return icon == null ? DIR_ICON : icon;
	}

	private Icon loadSystemIcon(final VirtualFileWrapper wrapper) throws IOException {
		if (LINUX) {
			return FILE_ICON;
		}
		String ext = "." + wrapper.getVirtualFile().getUrl().getExtension();
		File file = File.createTempFile("icon" + System.currentTimeMillis(), ext);
		Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
		file.delete();
		return icon;
	}
}