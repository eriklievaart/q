package com.eriklievaart.q.tcp.tunnel;

import java.net.Socket;
import java.net.SocketException;
import java.util.function.Supplier;

import com.eriklievaart.q.tcp.shared.TcpDisconnectException;
import com.eriklievaart.q.tcp.shared.TcpTransfer;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;

public class SocketTunnel implements TcpTunnel {

	private TcpTransfer transfer;

	public SocketTunnel(Socket socket) {
		this.transfer = TcpTransfer.from(socket);
	}

	@Override
	public void sendVO(TunnelVO vo) {
		rewrapExceptions(() -> {

			transfer.writeString(vo.getCommandLine());
			transfer.writeInt(vo.bytes);
			transfer.writeBytes(vo.buffer, vo.bytes);

			return null;
		});
	}

	@Override
	public TunnelVO receiveVO() {
		return rewrapExceptions(() -> {

			String command = transfer.readString();
			TunnelCommand type = TunnelCommand.valueOf(command.replaceFirst(" .*", ""));

			TunnelVO vo = new TunnelVO(type);
			vo.args = command.replaceFirst("\\S++\\s*+", "");
			vo.bytes = transfer.readInt();
			if (vo.bytes > 0) {
				vo.buffer = transfer.readBytes(vo.bytes);
			}
			return vo;
		});
	}

	private <E> E rewrapExceptions(Supplier<E> runnable) {
		try {
			return runnable.get();

		} catch (RuntimeIOException e) {
			if (e.getCause() instanceof SocketException) {
				throw new TcpDisconnectException();
			} else {
				throw e;
			}
		}
	}
}
