package com.eriklievaart.q.zcopy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("copy one or more files from any filesystem to any writable filesystem")
class CopyShellCommand implements Invokable {
	private static final String COPY_CHILD_MSG = "% => %; copying to a child of itself!";

	private final LogTemplate virtual = new LogTemplate("virtual.files");

	private Mode mode;
	private VirtualFile singleFile;
	private String singleRename;
	private VirtualFile destinationDir;
	private List<VirtualFile> urls;

	private enum Mode {
		SINGLE, URLS;
	}

	@Flag(group = "main", values = { "$url", "$dir~", "``" })
	@Doc("Copy a file or directory. Three arguments: 1) file to copy 2) new parent. 3) new file name (if not empty)")
	public CopyShellCommand single(final VirtualFile file, final VirtualFile destination, final String name) {
		singleFile = file;
		destinationDir = destination;
		singleRename = name;
		mode = Mode.SINGLE;

		return this;
	}

	@Flag(group = "main", values = { "$urls", "$dir~" }, primary = true)
	@Doc("Copy multiple urls. Two arguments 1) urls to copy 2) directory to copy urls into")
	public CopyShellCommand urls(final List<VirtualFile> list, final VirtualFile destination) {
		this.destinationDir = destination;
		this.urls = list;

		mode = Mode.URLS;
		return this;
	}

	@Override
	public void invoke(PluginContext context) {
		Check.isTrue(mode != null, "No flags were set!");

		switch (mode) {
		case SINGLE:
			copySingle();
			return;

		case URLS:
			copyUrls();
			return;

		default:
			throw new IllegalStateException("Unknown enum constant: " + mode);
		}
	}

	private void copyUrls() {
		for (VirtualFile file : urls) {
			copyFile(file, destinationDir.resolve(file.getName()));
		}
	}

	private void copySingle() {
		copyFile(singleFile, getDestinationSingle());
	}

	private void copyFile(VirtualFile source, VirtualFile destination) {
		virtual.info("copy $ -> $", source.getUrl().getUrlUnescaped(), destination.getUrl().getUrlUnescaped());
		validateNoCopyToChild(source, destination);
		source.copyTo(destination);
	}

	private VirtualFile getDestinationSingle() {
		String rename = Str.defaultIfEmpty(singleRename, singleFile.getUrl().getNameEscaped());
		VirtualFile specifiedDestination = destinationDir.resolve(rename);
		if (!singleFile.equals(specifiedDestination)) {
			return specifiedDestination;
		}
		VirtualFile stampedFile = destinationDir.resolve(getTimestampedName());
		Check.isFalse(stampedFile.exists(), "% exists!", stampedFile.getName());
		return stampedFile;
	}

	private String getTimestampedName() {
		String ext = singleFile.getExtension();
		String base = singleFile.getBaseName() + "-" + System.currentTimeMillis();
		return Str.isBlank(ext) ? base : base + "." + ext;
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		boolean directory = !destinationDir.exists() || destinationDir.isDirectory();
		PluginException.unless(directory, "Not a directory: $", destinationDir);
		validateMode();
	}

	private void validateMode() throws PluginException {
		switch (mode) {

		case SINGLE:
			validateSingle();
			return;

		case URLS:
			validateUrls();
			return;

		default:
			throw new IllegalStateException("Unknown enum constant: " + mode);
		}
	}

	private void validateSingle() throws PluginException {
		PluginException.unless(singleFile.exists(), "File does not exist: %", singleFile);
		validateNoCopyToChild(singleFile, destinationDir);
		PluginException.on(singleFile.equals(destinationDir), "source == destination");
	}

	private void validateUrls() throws PluginException {
		Set<String> duplicates = getDuplicateNames();
		PluginException.unless(duplicates.isEmpty(), "duplicates: $", duplicates);
		for (VirtualFile file : urls) {
			validateNoCopyToChild(file, destinationDir);
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

	private void validateNoCopyToChild(final VirtualFile source, final VirtualFile destination) {
		boolean copyToChild = source.getUrl().isParentOf(destination.getUrl());
		Check.isFalse(copyToChild, COPY_CHILD_MSG, source.getUrl(), destination.getUrl());
	}
}
