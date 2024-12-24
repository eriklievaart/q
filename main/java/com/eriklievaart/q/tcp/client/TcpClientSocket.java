package com.eriklievaart.q.tcp.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.tcp.shared.chunk.TcpChunkOutputStream;
import com.eriklievaart.q.tcp.tunnel.ConcurrentTunnel;
import com.eriklievaart.q.tcp.tunnel.LoggingTunnel;
import com.eriklievaart.q.tcp.tunnel.SocketTunnel;
import com.eriklievaart.q.tcp.tunnel.TcpTunnel;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.Box2;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpClientSocket implements Runnable {
	private LogTemplate log = new LogTemplate(getClass());

	private Socket socket;
	private TcpTunnel innerTunnel;
	private ConcurrentTunnel outerTunnel;
	private ServiceCollection<QMainUi> ui;
	private Map<String, Box2<String, String>> cache = new Hashtable<>();
	private AtomicReference<String> root = new AtomicReference<>();

	public TcpClientSocket(ServiceCollection<QMainUi> ui, Socket socket) {
		Check.noneNull(ui, socket);
		this.ui = ui;
		this.socket = socket;
		log.info(this.socket);

		initChannel();
	}

	private void initChannel() {
		innerTunnel = new LoggingTunnel(new SocketTunnel(socket));
		outerTunnel = new ConcurrentTunnel(innerTunnel);
	}

	public void setInitialLocation(String location) {
		root.set(location);
	}

	@Override
	public void run() {
		outerTunnel.block(() -> {
			try {
				init();

				if (socket != null) {
					socket.setSoTimeout(10 * 1000);
					navigateToRoot();
				}

			} catch (IOException e) {
				log.debug(e);
			}
		});
	}

	private void init() {
		log.info("pending server accept");

		if (innerTunnel.receiveVO().getArgsAsBoolean()) {
			log.info("connection accepted!");

		} else {
			log.info("connection rejected!");
			close();
		}
	}

	private void navigateToRoot() {
		if (Str.isBlank(root.get())) {
			innerTunnel.sendVO(new TunnelVO(TunnelCommand.ROOT));
			root.set(innerTunnel.receiveVO().args);
		}
		log.debug("remote root: $", root.get());
		checkIsValidPath(root.get());
		ui.oneCall(u -> u.navigateFuzzy("right", "tcp://" + root.get()));
	}

	public void close() {
		log.trace("closing socket");

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				log.warn(e);
			}
			socket = null;
		}
		outerTunnel = null;
		innerTunnel = null;
		cache.clear();
	}

	public TunnelVO sendRequest(TunnelVO vo) {
		return outerTunnel.sendAndReceiveVO(vo);
	}

	public TunnelVO sendCachedRequest(TunnelVO request) {
		String key = request.getCommandLine();

		outerTunnel.tryLock(tunnel -> {
			// connection isn't busy, so might as well ask server for latest info
			TunnelVO vo = tunnel.sendAndReceiveVO(request);
			cache.put(key, new Box2<>(vo.args, vo.getBodyAsString()));
		});
		if (!cache.containsKey(key)) {
			// not in cache, so we HAVE to call the server synchronously
			TunnelVO vo = outerTunnel.sendAndReceiveVO(request);
			cache.put(key, new Box2<>(vo.args, vo.getBodyAsString()));
		}
		Box2<String, String> cached = cache.get(key);
		TunnelVO result = new TunnelVO(TunnelCommand.RESPONSE, cached.getFirst());
		result.setBody(cached.getSecond());
		return result;
	}

	public void runWithLock(Consumer<TcpTunnel> consumer) {
		outerTunnel.block(() -> {
			consumer.accept(innerTunnel);
		});
	}

	public OutputStream getOutputStream(TunnelVO request) {
		Check.isEqual(request.command, TunnelCommand.TO_SERVER);
		outerTunnel.getLock().lock();
		innerTunnel.sendVO(request);
		return new TcpChunkOutputStream(innerTunnel, outerTunnel.getLock());
	}

	private void checkIsValidPath(String path) {
		Check.isEmpty(UrlTool.getProtocol(path), "invalid path: " + path);
	}

	public boolean notConnected() {
		return socket == null;
	}
}
