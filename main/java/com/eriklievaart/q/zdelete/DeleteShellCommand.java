package com.eriklievaart.q.zdelete;

import java.util.List;
import java.util.Optional;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("delete files on any supported Filesystem.")
class DeleteShellCommand implements Invokable {

	private final LogTemplate virtual = new LogTemplate("virtual.files");
	private final TrashCache cache;

	private VirtualFile file;
	private List<VirtualFile> urls;
	private Mode mode;
	private boolean permanent = false;

	public DeleteShellCommand(TrashCache cache) {
		this.cache = cache;
	}

	@Flag(group = "main", values = { "$url" })
	@Doc("Delete a single file or directory. One argument: url to delete")
	public DeleteShellCommand single(final VirtualFile value) {
		this.file = value;
		mode = Mode.SINGLE;
		return this;
	}

	@Flag(group = "main", values = { "$urls" }, primary = true)
	@Doc("Delete multiple urls. One argument: urls to delete")
	public DeleteShellCommand urls(final List<VirtualFile> value) {
		this.urls = value;
		mode = Mode.URLS;
		return this;
	}

	@Flag(group = "")
	@Doc("delete permanently, do not send to trash")
	public DeleteShellCommand permanent() {
		permanent = true;
		return this;
	}

	@Override
	public void validate(PluginContext context) {
	}

	@Override
	public void invoke(PluginContext context) {
		switch (mode) {
		case SINGLE:
			deleteSingle(file);
			return;

		case URLS:
			deleteUrls();
			return;

		default:
			throw new AssertionException("Unknown enum constant: %", mode);
		}
	}

	private void deleteUrls() {
		for (VirtualFile vf : urls) {
			deleteSingle(vf);
		}
	}

	private void deleteSingle(final VirtualFile remove) {
		Optional<SystemFile> trash = cache.getTrashLocation(remove);
		boolean deletePermanent = permanent || containsTrash(remove.getPath()) || !trash.isPresent();

		if (deletePermanent) {
			virtual.info("delete $ -> /dev/null", remove.getUrl().getUrlUnescaped());
			remove.delete();
		} else {
			SystemFile destination = getTrashFile(trash.get(), remove.getName());
			virtual.info("delete $ -> $", remove.getUrl().getUrlUnescaped(), destination.getUrl().getUrlUnescaped());
			remove.moveTo(destination);
		}
	}

	private boolean containsTrash(String path) {
		return path.contains("/Trash/") || path.contains("/.Trash");
	}

	private SystemFile getTrashFile(SystemFile trashRoot, String name) {
		SystemFile destination = trashRoot.resolve(name);
		if (!destination.exists()) {
			return destination;
		}
		destination = trashRoot.resolve(name + "-" + System.nanoTime());
		RuntimeIOException.on(destination.exists(), "Cannot move % to trash, file already in trash", name);
		return destination;
	}

	private enum Mode {
		SINGLE, URLS
	}
}
