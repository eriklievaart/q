package com.eriklievaart.q.zmove;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("move one or more files from any filesystem to any writable filesystem")
class MoveShellCommand implements Invokable {
	private static final String MOVE_TO_CHILD_MSG = "% => %; cannot move to a child of itself";

	private final LogTemplate log = new LogTemplate(getClass());
	private final LogTemplate virtual = new LogTemplate("virtual.files");

	private VirtualFile source;
	private VirtualFile destination;
	private String rename;
	private Mode mode;

	private List<VirtualFile> urls;

	@Flag(group = "main", values = { "$url", "$dir~", "``" })
	@Doc("Move a file or directory. Three arguments: 1) file to move 2) new parent. 3) new file name (if not empty)")
	public MoveShellCommand single(final VirtualFile file, final VirtualFile dir, final String name) {
		source = file;
		destination = dir;
		rename = name;

		mode = Mode.SINGLE;
		return this;
	}

	@Flag(group = "main", values = { "$urls", "$dir~" }, primary = true)
	@Doc("Move multiple urls. Two arguments 1) urls to move 2) directory to move urls into")
	public MoveShellCommand urls(final List<VirtualFile> list, final VirtualFile destinationDir) {
		this.destination = destinationDir;
		this.urls = list;

		mode = Mode.URLS;
		return this;
	}

	@Override
	public void invoke(PluginContext context) {
		switch (mode) {

		case SINGLE:
			moveSingleFile();
			return;

		case URLS:
			destination.mkdir();
			moveUrls();
			return;

		default:
			throw new RuntimeException("Unknown enum constant: " + mode);
		}
	}

	private void moveUrls() {
		for (VirtualFile file : urls) {
			VirtualFile resolved = destination.resolve(file.getName());
			virtual.info("move $ -> $", file.getUrl().getUrlUnescaped(), resolved.getUrl().getUrlUnescaped());
			moveVirtualFile(file, resolved);
		}
	}

	private void moveSingleFile() {
		String name = Str.defaultIfEmpty(rename, source.getName());
		VirtualFile resolved = destination.resolve(name);
		virtual.info("move $ -> $", source.getUrl().getUrlUnescaped(), resolved.getUrl().getUrlUnescaped());
		moveVirtualFile(source, resolved);
	}

	private void moveVirtualFile(final VirtualFile from, final VirtualFile to) {
		if (from.equals(to)) {
			return;
		}
		validateNoCopyToChild(from, to);

		log.trace("$ => $", from, to);
		if (!from.isDirectory() || !to.exists()) {
			from.moveTo(to);
			return;
		}
		for (VirtualFile child : from.getChildren()) {
			moveVirtualFile(child, to.resolve(child.getName()));
		}
		from.delete();
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		boolean canWrite = !destination.exists() || destination.isDirectory();
		PluginException.unless(canWrite, "Invalid destination: %", destination);

		switch (mode) {
		case URLS:
			validateUrls();
			return;

		case SINGLE:
			PluginException.unless(source.exists(), "File does not exist: %", source);
			return;

		default:
			throw new FormattedException("% unknown enum constant", mode);
		}
	}

	private void validateUrls() throws PluginException {
		Set<String> duplicates = getDuplicateNames();
		PluginException.unless(duplicates.isEmpty(), "duplicates: $", duplicates);
		for (VirtualFile file : urls) {
			validateNoCopyToChild(file, destination);
		}
	}

	private Set<String> getDuplicateNames() {
		Set<String> unique = new HashSet<>();
		Set<String> duplicates = new HashSet<>();
		for (VirtualFile file : urls) {
			if (unique.contains(file.getName())) {
				duplicates.add(file.getName());
			}
			unique.add(file.getName());
		}
		return duplicates;
	}

	private void validateNoCopyToChild(final VirtualFile from, final VirtualFile to) {
		boolean copyToChild = from.getUrl().isParentOf(to.getUrl());
		Check.isFalse(copyToChild, MOVE_TO_CHILD_MSG, from.getUrl(), to.getUrl());
	}

	private enum Mode {
		SINGLE, URLS;
	}
}
