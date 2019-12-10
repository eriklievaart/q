package com.eriklievaart.q.ui.config;

import java.io.File;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class UiResourcePaths {
	public static final String FAVICON = "/ui/local/icons/favicon.png";
	public static final String BINDINGS = "/ui/ui-bindings.txt";

	private LogTemplate log = new LogTemplate(getClass());
	private File root;

	public UiResourcePaths(File root) {
		log.info("config root: $", root);
		this.root = root;
	}

	public File getFileOperationLog() {
		return new File(root, "files.log");
	}

	public File getWindowSaverConfig() {
		return new File(root, "data/ui/windowsaver.ini");
	}

	public File getLruCache() {
		return new File(root, "data/ui/visited.txt");
	}

	public File getMimeTypes() {
		return new File(root, "data/ui/icon/mime.types.ini");
	}

	public File getMimeFallbacks() {
		return new File(root, "data/ui/icon/mime.fallback.ini");
	}

	public File getIconDirectory() {
		return new File(root, "data/ui/icon/image");
	}

	public File getDocumentationStart() {
		return new File(UrlTool.append(root.getAbsolutePath(), "doc/introduction.xhtml"));
	}

	public File getIconFile(String relative) {
		return new File(getIconDirectory(), relative);
	}
}