package com.eriklievaart.q.tcp.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import com.eriklievaart.q.tcp.shared.TcpDisconnectException;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.AbstractVirtualFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFileContent;

public class TcpFile extends AbstractVirtualFile {

	private String path;
	private TcpFileType type;
	private TcpProtocolResolver remote;

	public TcpFile(TcpProtocolResolver resolver, String path, TcpFileType type) {
		this.remote = resolver;
		this.path = path;
		this.type = type;

		Check.isFalse(path.startsWith("tcp://"), "invalid path %; supply base path only", path);
	}

	@Override
	public String getProtocol() {
		return "tcp";
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public VirtualFile resolve(String name) {
		return remote.resolve(UrlTool.append(getPath(), name));
	}

	@Override
	public Optional<? extends VirtualFile> getParentFile() {
		String parent = UrlTool.getParent(path);
		if (parent == null || parent.length() >= path.length()) {
			return Optional.empty();
		}
		return Optional.of(remote.resolve(parent));
	}

	@Override
	public List<? extends VirtualFile> getChildren() {
		try {
			return remote.list(path);
		} catch (TcpDisconnectException e) {
			return NewCollection.list();
		}
	}

	@Override
	public boolean isFile() {
		return type == TcpFileType.FILE;
	}

	@Override
	public boolean isDirectory() {
		return type == TcpFileType.DIRECTORY;
	}

	@Override
	public boolean exists() {
		return type != TcpFileType.MISSING;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public long length() {
		return 0;
	}

	@Override
	public void copyTo(VirtualFile to) {
		log.info("copy $ -> $", this, to);
		Check.isFalse(to instanceof TcpFile, "copy remote file to remote file not supported!");

		if (isFile()) {
			copyFileTo(to);
		} else if (isDirectory()) {
			copyDirectoryTo(to);
		} else {
			throw new AssertionException("invalid file: " + this);
		}
	}

	private void copyFileTo(VirtualFile to) {
		boolean destinationIsFile = !to.exists() || to.isFile();
		VirtualFile destination = destinationIsFile ? to : to.resolve(getName());

		try {
			try (OutputStream os = destination.getContent().getOutputStream()) {
				remote.download(path, os);
			}

		} catch (IOException e) {
			destination.delete();
			throw new RuntimeIOException(e);
		}
	}

	private void copyDirectoryTo(VirtualFile to) {
		if (!to.exists()) {
			to.mkdir();
		}
		for (VirtualFile child : getChildren()) {
			if (child.isDirectory()) {
				child.copyTo(to.resolve(child.getName()));
			} else {
				child.copyTo(to);
			}
		}
	}

	@Override
	public OutputStream getOutputStream() {
		Check.isTrue(isFile(), "not a file!");
		return remote.getOutputStream(getPath());
	}

	@Override
	public VirtualFileContent getContent() {
		return new VirtualFileContent() {
			@Override
			public OutputStream getOutputStream() {
				Check.isTrue(isFile() || !exists(), "not a file!");
				return remote.getOutputStream(getPath());
			}

			@Override
			public InputStream getInputStream() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public boolean createFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean mkdir() {
		boolean success = remote.mkdir(path);
		if (success) {
			type = TcpFileType.DIRECTORY;
		}
		return success;
	}

	@Override
	public long lastModified() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLastModified(long stamp) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete() {
		remote.delete(getPath());
	}

	@Override
	public InputStream getInputStream() {
		throw new UnsupportedOperationException();
	}
}
