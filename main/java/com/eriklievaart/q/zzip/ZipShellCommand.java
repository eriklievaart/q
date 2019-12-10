package com.eriklievaart.q.zzip;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;
import com.eriklievaart.toolkit.vfs.api.pack.VirtualFileZipper;

@Doc("create zip files")
public class ZipShellCommand implements Invokable {

	private VirtualFile singleFile;
	private VirtualFile destinationDir;
	private ZipMode mode = ZipMode.ZIP;
	private ZipController controller;

	public ZipShellCommand(ZipController controller) {
		Check.notNull(controller);
		this.controller = controller;
	}

	@Flag(group = "main", values = { "$url", "$dir~" }, primary = true)
	@Doc("Zip a file or directory. Two arguments: 1) file to zip 2) new parent.")

	public ZipShellCommand single(final VirtualFile file, final VirtualFile destination) {
		singleFile = file;
		destinationDir = destination;
		mode = ZipMode.ZIP;
		return this;
	}

	@Flag(group = "main", values = { "$url", "$dir~" })
	@Doc("Unpack a zip file. Two arguments: 1) file to unzip 2) new parent.")
	public ZipShellCommand unpack(final VirtualFile file, final VirtualFile destination) {
		singleFile = file;
		destinationDir = destination;
		mode = ZipMode.UNPACK;
		return this;
	}

	@Flag(group = "main", values = { "$url" })
	@Doc("List the files in a zip file. One arguments: zip file")
	public ZipShellCommand list(final VirtualFile file) {
		singleFile = file;
		mode = ZipMode.LIST;
		return this;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		switch (mode) {

		case ZIP:
			VirtualFile zip = destinationDir.resolve(singleFile.getUrl().getBaseName() + ".zip");
			VirtualFileZipper.zip(singleFile, zip);
			return;

		case UNPACK:
			VirtualFileZipper.unzip(singleFile, destinationDir);
			return;

		case LIST:
			controller.show(VirtualFileZipper.entries(singleFile));
			return;
		}
		throw new RuntimeException("unknown mode: " + mode);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		PluginException.on(singleFile == null, "source is null");
		PluginException.unless(singleFile.exists(), "source does not exist: $", singleFile);

		if (mode == ZipMode.LIST) {
			PluginException.unless(singleFile.isFile(), "selected url is not a zip file: $", singleFile);
		}
		if (mode != ZipMode.LIST) {
			PluginException.on(destinationDir == null, "destination is null");
			PluginException.unless(destinationDir.exists(), "destination does not exist: $", destinationDir);
		}
	}

	private enum ZipMode {
		ZIP, UNPACK, LIST;
	}
}