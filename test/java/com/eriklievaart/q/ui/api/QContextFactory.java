package com.eriklievaart.q.ui.api;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class QContextFactory {

	private MemoryFileSystem memory = new MemoryFileSystem();
	private VirtualFile leftDir = memory.resolve("sandbox/dir1");
	private VirtualFile rightDir = memory.resolve("sandbox/dir2");
	private List<VirtualFile> leftUrls = NewCollection.list();
	private List<VirtualFile> rightUrls = NewCollection.list();

	public QContextFactory leftDir(String path) {
		leftDir = memory.resolve(path);
		return this;
	}

	public QContextFactory leftDir(VirtualFile file) {
		leftDir = file;
		return this;
	}

	public QContextFactory leftSystemDir(String path) {
		leftDir = new SystemFile(path);
		return this;
	}

	public QContextFactory rightDir(String path) {
		rightDir = memory.resolve(path);
		return this;
	}

	public QContextFactory rightDir(VirtualFile file) {
		rightDir = file;
		return this;
	}

	public QContextFactory urlLeft(String path) {
		leftUrls.add(memory.resolve(path));
		return this;
	}

	public QContext make() {
		QContext context = new QContext(new BrowserContext(leftDir, leftUrls), new BrowserContext(rightDir, rightUrls));
		context.setLeftActive();
		return context;
	}
}