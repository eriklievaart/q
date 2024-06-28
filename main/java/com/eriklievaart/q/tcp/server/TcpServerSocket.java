package com.eriklievaart.q.tcp.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import com.eriklievaart.q.tcp.TcpDependencies;
import com.eriklievaart.q.tcp.shared.TcpCommand;
import com.eriklievaart.q.tcp.shared.TcpTransfer;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpServerSocket {
	private static final int QUESTION = JOptionPane.QUESTION_MESSAGE;
	private static final int OPTION = JOptionPane.YES_NO_CANCEL_OPTION;
	private static Set<String> autoAccept = new HashSet<>();
	private LogTemplate log = new LogTemplate(getClass());

	private Socket socket;
	private TcpTransfer transfer;
	private TcpDependencies dependencies;

	public TcpServerSocket(TcpDependencies dependencies, Socket client) {
		this.dependencies = dependencies;
		this.socket = client;
		this.transfer = TcpTransfer.from(socket);
	}

	public void listen() {
		try {
			if (!confirmAcceptSocket(socket.getRemoteSocketAddress())) {
				log.info("rejecting client!");
				transfer.writeString("reject");
				socket.close();
				socket = null;
				return;
			}
			log.info("accepting client!");
			dependencies.controller.cleanup();
			handleRequests();

		} catch (Exception e) {
			log.warn(e);
			dependencies.controller.receivedRequest("*error:* " + e.getMessage());
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

		transfer.writeString("accept");

		while (true) {
			String received = transfer.readString();
			if (received == null) {
				log.info("socket be dead!");
				return;
			}
			dependencies.controller.receivedRequest(received);
			String[] commandAndArgs = received.split(" ++", 2);
			handleRequest(commandAndArgs[0], commandAndArgs.length > 1 ? commandAndArgs[1] : "no args sent");
		}
	}

	private void handleRequest(String command, String args) {
		TcpCommand.valueOf(command).invoke(dependencies, transfer, args);
	}
}
