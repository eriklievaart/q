package com.eriklievaart.q.tcp.shared;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.mock.BombSquad;

public class TcpTransferU {

	@Test
	public void transmitStrings() throws IOException {
		String data = "1234abcd";

		ByteArrayOutputStream transmitterOut = new ByteArrayOutputStream();
		TcpTransfer transmitter = new TcpTransfer(new ByteArrayInputStream(new byte[1024]), transmitterOut);
		transmitter.writeString(data);

		ByteArrayInputStream receiverIn = new ByteArrayInputStream(transmitterOut.toByteArray());
		TcpTransfer receiver = new TcpTransfer(receiverIn, new ByteArrayOutputStream());
		Check.isEqual(receiver.readString(), data);
	}

	@Test
	public void transmitOutputStream() throws IOException {
		String data = "1234abcd";

		ByteArrayOutputStream transmitterOut = new ByteArrayOutputStream();
		TcpTransfer transmitter = new TcpTransfer(new ByteArrayInputStream(new byte[1024]), transmitterOut);
		transmitter.upload(StreamTool.toInputStream(data));

		ByteArrayInputStream receiverIn = new ByteArrayInputStream(transmitterOut.toByteArray());
		TcpTransfer receiver = new TcpTransfer(receiverIn, new ByteArrayOutputStream());

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		receiver.download(buffer);
		Check.isEqual(new String(buffer.toByteArray()), data);
	}

	@Test
	public void transmitOutputStreamCorrupted() throws IOException {
		String data = "1234abcd";

		ByteArrayOutputStream transmitterOut = new ByteArrayOutputStream();
		TcpTransfer transmitter = new TcpTransfer(new ByteArrayInputStream(new byte[1024]), transmitterOut);
		transmitter.upload(StreamTool.toInputStream(data));

		byte[] bytes = transmitterOut.toByteArray();
		bytes[8] = 8; // corruption
		TcpTransfer receiver = new TcpTransfer(new ByteArrayInputStream(bytes), new ByteArrayOutputStream());

		BombSquad.diffuse("file corrupted", () -> {
			receiver.download(new ByteArrayOutputStream());
		});
	}
}
