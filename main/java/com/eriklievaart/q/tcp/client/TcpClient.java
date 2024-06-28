package com.eriklievaart.q.tcp.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.tcp.vfs.TcpFile;
import com.eriklievaart.q.tcp.vfs.TcpFileType;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TcpClient {
	private LogTemplate log = new LogTemplate(getClass());

	private ServiceCollection<QMainUi> ui;
	private TcpClientSocket socket;
	private TcpHosts hosts;

	public TcpClient(TcpHosts hosts, ServiceCollection<QMainUi> ui) {
		this.hosts = hosts;
		this.ui = ui;
	}

	public TcpFileType getType(String path) {
		if (socket == null) {
			throw new RuntimeIOException("TCP socket has been closed!");
		}
		return socket.info(path);
	}

	public List<TcpVO> list(String path) {
		if (socket == null) {
			throw new RuntimeIOException("TCP socket has been closed!");
		}
		List<TcpVO> list = socket.list(path);
		return list;
	}

	public void download(String path, OutputStream os) throws IOException {
		Check.notNull(socket);
		socket.download(path, os);
	}

	public OutputStream getOutputStream(String path) {
		Check.notNull(socket);
		return socket.getOutputStream(path);
	}

	public boolean mkdir(String path) {
		return socket.mkdir(path);
	}

	public void delete(String path) {
		socket.delete(path);
	}

	public void connect() {
		String message = "supply remote [host] or [host]:[port]";
		ui.oneCall(u -> u.getDialogs().input(message, hosts.getMostRecent(), address -> {
			closeSocket();
			log.info("connecting to host: $", address);
			openSocket(address);
			hosts.add(address);
		}));
	}

	public void reconnect() {
		openSocket(hosts.getMostRecent());
	}

	public void disconnect() {
		closeSocket();
		navigateTcpUrlsToRoot();
	}

	private void navigateTcpUrlsToRoot() {
		String root = "file://" + FileSystemView.getFileSystemView().getRoots()[0].getAbsolutePath();

		VirtualFile left = ui.oneReturns(u -> u.getQContext().getLeft().getDirectory());
		if (left instanceof TcpFile) {
			ui.oneCall(u -> {
				u.navigateFuzzy("left", root);
			});
		}
		VirtualFile right = ui.oneReturns(u -> u.getQContext().getRight().getDirectory());
		if (right instanceof TcpFile) {
			ui.oneCall(u -> u.navigateFuzzy("right", "file:///"));
		}
	}

	private void openSocket(String address) {
		int port = address.contains(":") ? Integer.parseInt(address.replaceFirst("[^:]++:", "")) : 9090;
		String ip = address.replaceFirst(":.*", "");
		openSocket(ip, port);
	}

	private void openSocket(String ip, int port) {
		try {
			log.info("connecting to $:$", ip, port);
			socket = new TcpClientSocket(ui, new Socket(ip, port));
			Thread.sleep(100);
			new Thread(socket).start();

		} catch (IOException | InterruptedException e) {
			log.debug(e);
			e.printStackTrace();
		}
	}

	private void closeSocket() {
		if (socket == null) {
			return;
		}
		socket.close();
		socket = null;
	}

	public boolean notConnected() {
		return socket == null || socket.notConnected();
	}
}
