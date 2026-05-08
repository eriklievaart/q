package com.eriklievaart.q.tcp.vfs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import com.eriklievaart.q.tcp.client.TcpClient;
import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TcpProtocolResolver implements ProtocolResolver {
	private LogTemplate log = new LogTemplate(getClass());

	private TcpClient remote;

	public TcpProtocolResolver(TcpClient remote) {
		this.remote = remote;
	}

	@Override
	public String getProtocol() {
		return "tcp";
	}

	@Override
	public VirtualFile resolve(String url) {
		log.trace("resolve $", url);
		Optional<String> protocol = UrlTool.getProtocol(url);
		Check.isTrue(protocol.isEmpty() || protocol.get().equals(getProtocol()), "invalid protocol %", protocol);
		String path = UrlTool.getPath(url);
		return new TcpFile(this, path, remote.getType(path));
	}

	public List<? extends VirtualFile> list(String path) {
		if (remote.notConnected()) {
			log.trace("not connected!");
			return NewCollection.list();
		}
		log.trace("list $", path);
		return ListTool.map(remote.list(path), vo -> new TcpFile(this, UrlTool.append(path, vo.name), vo.type));
	}

	public void download(String path, OutputStream os) throws IOException {
		remote.download(path, os);
	}

	public OutputStream getOutputStream(String path) {
		return remote.getOutputStream(path);
	}

	public boolean mkdir(String path) {
		return remote.mkdir(path);
	}

	public void delete(String path) {
		remote.delete(path);
	}
}
