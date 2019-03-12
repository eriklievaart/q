package com.eriklievaart.q.zzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.FromCollection;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("create zip files")
public class ZipShellCommand implements Invokable {

	private VirtualFile singleFile;
	private VirtualFile destinationDir;

	@Flag(group = "main", values = { "$url", "$dir~" }, primary = true)
	@Doc("Zip a file or directory. Two arguments: 1) file to zip 2) new parent.")
	public ZipShellCommand single(final VirtualFile file, final VirtualFile destination) {
		singleFile = file;
		destinationDir = destination;
		return this;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
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
		PluginException.on(destinationDir == null, "destination is null");
		PluginException.unless(singleFile.exists(), "source does not exist: " + singleFile);
		PluginException.unless(destinationDir.exists(), "destination does not exist: " + destinationDir);
	}
}
