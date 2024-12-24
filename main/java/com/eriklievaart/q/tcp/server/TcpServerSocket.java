package com.eriklievaart.q.tcp.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import com.eriklievaart.q.tcp.TcpDependencies;
import com.eriklievaart.q.tcp.shared.TcpDisconnectException;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.tcp.tunnel.LoggingTunnel;
import com.eriklievaart.q.tcp.tunnel.SocketTunnel;
import com.eriklievaart.q.tcp.tunnel.TcpTunnel;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpServerSocket {
	private static final boolean ALWAYS_ACCEPT = Str.isEqual(System.getenv("q_tcp_always"), "true");
	private static final AtomicBoolean ALWAYS_ACCEPT_ENABLED = new AtomicBoolean(false);
	private static final int QUESTION = JOptionPane.QUESTION_MESSAGE;
	private static final int OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
	private static Set<String> autoAccept = new HashSet<>();
	private LogTemplate log = new LogTemplate(getClass());

	private Socket socket;
	private TcpDependencies dependencies;
	private TcpTunnel tunnel;
	private TcpServerHandler handler;

	public TcpServerSocket(TcpDependencies dependencies, Socket client) {
		this.dependencies = dependencies;
		this.socket = client;
		this.tunnel = new LoggingTunnel(new SocketTunnel(socket));
		this.handler = new TcpServerHandler(dependencies, tunnel);
	}

	public void listen() {
		try {
			if (!confirmAcceptSocket(socket.getRemoteSocketAddress())) {
				log.info("rejecting client!, $", socket);
				tunnel.sendVO(new TunnelVO(TunnelCommand.ACCEPT, "false"));
				socket.close();
				socket = null;
				return;
			}
			log.info("accepting client!");
			tunnel.sendVO(new TunnelVO(TunnelCommand.ACCEPT, "true"));
			dependencies.controller.cleanup();
			handleRequests();

		} catch (TcpDisconnectException e) {
			dependencies.controller.receivedRequest(new TunnelVO(TunnelCommand.ERROR, "disconnected"));

		} catch (Exception e) {
			log.warn(e);
			dependencies.controller.receivedRequest(new TunnelVO(TunnelCommand.ERROR, "*error:* " + e.getMessage()));
		}
	}

	private boolean confirmAcceptSocket(SocketAddress remote) {
		log.info("Incoming TCP connection: $", remote);

		String ip = getIp(remote.toString());
		if (autoAccept.contains(ip)) {
			return true;
		}
		String msg = Str.sub("accept connection from $?", ip);
		Object[] buttons = new Object[] { "yes", "session", "no" };

		if (ALWAYS_ACCEPT && ALWAYS_ACCEPT_ENABLED.get()) {
			return true;
		}
		int selected = JOptionPane.showOptionDialog(null, msg, "", OPTION, QUESTION, null, buttons, null);
		if (selected == 1) {
			autoAccept.add(ip);
		}
		return selected != 2;
	}

	private String getIp(String toStr) {
		return toStr.replaceFirst("\\D*+(\\d++[.]\\d++[.]\\d++[.]\\d++).*+", "$1");
	}

	private void handleRequests() throws IOException {
		while (true) {

			TunnelVO vo = tunnel.receiveVO();
			if (vo == null) {
				log.info("socket be dead!");
				return;
			}
			dependencies.controller.receivedRequest(vo);

			try {
				handler.process(vo);
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	private void handleException(Exception e) {
		log.error(e.getMessage(), e);
		tunnel.sendVO(new TunnelVO(TunnelCommand.ERROR, e.getMessage()));
	}
}
