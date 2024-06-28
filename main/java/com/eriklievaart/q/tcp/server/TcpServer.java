package com.eriklievaart.q.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.eriklievaart.q.tcp.TcpDependencies;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpServer extends Thread {
	private LogTemplate log = new LogTemplate(getClass());

	private AtomicBoolean open = new AtomicBoolean(false);
	private AtomicBoolean shutdown = new AtomicBoolean(false);

	private ServerSocket server;

	private TcpDependencies dependencies;

	public TcpServer(TcpDependencies dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public void run() {
		try {
			while (!shutdown.get()) {
				if (open.get() && server == null) {
					openSocket();

				} else {
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
	}

	private void openSocket() {
		try {
			synchronized (this) {
				server = new ServerSocket(9090);
			}
			while (server != null) {
				Socket client = server.accept();
				new Thread(() -> new TcpServerSocket(dependencies, client).listen()).start();
			}
		} catch (IOException ioe) {
			log.warn(ioe);
		}
	}

	private void closeSocket() {
		if (server != null) {
			open.set(false);
			synchronized (this) {
				try {
					server.close();
				} catch (IOException e) {
					log.warn(e);
				}
			}
		}
		server = null;
	}

	public void shutdown() {
		log.info("shutting down TCP server");
		shutdown.set(true);
		stopServer();
	}

	public void startServer() {
		log.info("starting TCP server");
		open.set(true);
	}

	public void stopServer() {
		log.info("stopping TCP server");
		closeSocket();
	}
}