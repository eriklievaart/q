package com.eriklievaart.q.tcp.shared;

public class TcpDisconnectException extends RuntimeException {

	public static void on(boolean b) {
		if (b) {
			throw new TcpDisconnectException();
		}
	}
}
