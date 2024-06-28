package com.eriklievaart.q.tcp.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.tcp.shared.TcpCommand;
import com.eriklievaart.q.tcp.shared.TcpTransfer;
import com.eriklievaart.q.tcp.vfs.TcpFileType;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.io.api.sha1.Sha1OutputStream;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.function.TryRunnable;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpClientSocket implements Runnable {
	private LogTemplate log = new LogTemplate(getClass());
	private Lock mutex = new ReentrantLock();

	private Socket socket;
	private TcpTransfer transfer;
	private ServiceCollection<QMainUi> ui;

	public TcpClientSocket(ServiceCollection<QMainUi> ui, Socket socket) {
		Check.noneNull(ui, socket);
		this.ui = ui;
		this.socket = socket;
		this.transfer = TcpTransfer.from(socket);
		log.info(this.socket);
	}

	@Override
	public void run() {
		try {
			mutex.lock();
			String root = init();
			log.debug("remote root: $", root);

			if (socket != null) {
				socket.setSoTimeout(10 * 1000);
				ui.oneCall(u -> u.navigateFuzzy("right", "tcp://" + root));
			}

		} catch (IOException e) {
			log.debug(e);
		} finally {
			mutex.unlock();
		}
	}

	private String init() {
		log.info("pending server accept");
		String line = transfer.readString();
		if (line.equals("accept")) {
			log.info("connection accepted!");
			transfer.writeString(TcpCommand.ROOT.toString());
			return transfer.readString();

		} else {
			log.info("connection rejected!");
			close();
			return null;
		}
	}

	public void close() {
		log.trace("closing socket");
		transfer = null;

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				log.warn(e);
			}
			socket = null;
		}
	}

	public void download(String path, OutputStream destination) throws IOException {
		checkIsValidPath(path);
		call(() -> {
			Check.notNull(socket, "disconnected!");
			transfer.writeString(TcpCommand.TO_CLIENT + " " + path);
			transfer.download(new Sha1OutputStream(destination));
			log.debug("download complete!");
		});
	}

	public OutputStream getOutputStream(String path) {
		checkIsValidPath(path);
		mutex.lock();
		transfer.writeString(TcpCommand.TO_SERVER + " " + path);
		return transfer.createOutputStream(() -> mutex.unlock());
	}

	public List<TcpVO> list(String path) {
		checkIsValidPath(path);
		return supply(() -> {
			List<TcpVO> result = NewCollection.list();
			transfer.writeString(Str.sub("$ $", TcpCommand.LS, path));

			transfer.readLines().forEach(line -> {
				String[] split = line.trim().split(" ++", 2);
				result.add(new TcpVO(split[1], TcpFileType.from(split[0])));
			});
			return result;
		});
	}

	public TcpFileType info(String path) {
		checkIsValidPath(path);
		return supply(() -> {
			transfer.writeString(Str.sub("$ $", TcpCommand.INFO, path));
			return TcpFileType.from(transfer.readString().trim());
		});
	}

	public boolean mkdir(String path) {
		checkIsValidPath(path);
		return supply(() -> {
			transfer.writeString(Str.sub("$ $", TcpCommand.MKDIR, path));
			return transfer.readBoolean();
		});
	}

	public boolean delete(String path) {
		checkIsValidPath(path);
		return supply(() -> {
			transfer.writeString(Str.sub("$ $", TcpCommand.DELETE, path));
			return transfer.readBoolean();
		});
	}

	private void checkIsValidPath(String path) {
		Check.isEmpty(UrlTool.getProtocol(path), "invalid path: " + path);
	}

	private void call(TryRunnable<IOException> runnable) throws IOException {
		Check.notNull(socket, "disconnected!");
		try {
			mutex.lock();
			runnable.invoke();
		} finally {
			mutex.unlock();
		}
	}

	private <E> E supply(Supplier<E> supplier) {
		Check.notNull(socket, "disconnected!");
		try {
			mutex.lock();
			return supplier.get();

		} finally {
			mutex.unlock();
		}
	}

	public boolean notConnected() {
		return socket == null;
	}
}
