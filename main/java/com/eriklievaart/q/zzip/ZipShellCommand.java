package com.eriklievaart.q.zzip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

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
			zip();
			return;

		case UNPACK:
			unzip();
			return;

		case LIST:
			showZipFileContents();
			return;
		}
		throw new RuntimeException("unknown mode: " + mode);
	}

	private void unzip() {
		try (ZipInputStream is = new ZipInputStream(singleFile.getContent().getInputStream())) {

			for (ZipEntry entry = is.getNextEntry(); entry != null; entry = is.getNextEntry()) {
				String name = entry.getName();

				if (entry.isDirectory()) {
					destinationDir.resolve(name).mkdir();

				} else {
					try (OutputStream os = destinationDir.resolve(name).getContent().getOutputStream()) {
						StreamTool.copyStreamNoClose(is, os);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeIOException("unable to read zip file $", e, singleFile);
		}
	}

	private void showZipFileContents() {
		controller.show(inspectZipFileNames());
	}

	List<String> inspectZipFileNames() {
		List<String> files = NewCollection.list();
		try (ZipInputStream is = new ZipInputStream(singleFile.getContent().getInputStream())) {
			for (ZipEntry entry = is.getNextEntry(); entry != null; entry = is.getNextEntry()) {
				files.add(entry.getName());
			}
		} catch (IOException e) {
			throw new RuntimeIOException("unable to read zip file $", e, singleFile);
		}
		return ListTool.sortedCopy(files);
	}

	private void zip() throws IOException {
		List<VirtualFile> list = listFiles(singleFile);

		VirtualFile zip = destinationDir.resolve(singleFile.getUrl().getBaseName() + ".zip");
		try (ZipOutputStream zos = new ZipOutputStream(zip.getContent().getOutputStream())) {
			for (VirtualFile file : list) {
				zos.putNextEntry(new ZipEntry(singleFile.getRelativePathOf(file)));
				copyFileContents(file, zos);
				zos.closeEntry();
			}
		}
	}

	private void copyFileContents(VirtualFile file, ZipOutputStream zos) {
		try (InputStream is = file.getContent().getInputStream()) {
			int length;
			byte[] buffer = new byte[1024];
			while ((length = is.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	private List<VirtualFile> listFiles(VirtualFile file) {
		if (file.isFile()) {
			return Arrays.asList(file);
		}
		return FromCollection.toList(new VirtualFileScanner(file).iterator());
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
