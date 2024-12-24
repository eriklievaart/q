package com.eriklievaart.q.tcp.shared;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.eriklievaart.q.tcp.shared.chunk.TcpChunks;
import com.eriklievaart.q.tcp.tunnel.MockTcpTunnel;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.io.api.sha1.Sha1;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.mock.BombSquad;

public class TcpChunksU {

	@Test
	public void sendChunks() {
		MockTcpTunnel tunnel = new MockTcpTunnel();
		String payload = "hello world!";
		TcpChunks.sendChunks(StreamTool.toInputStream(payload), tunnel);

		TunnelVO chunk = tunnel.popSent();
		Check.isEqual(chunk.command, TunnelCommand.CHUNK);
		Check.isEqual(chunk.getBodyAsString(), payload);

		TunnelVO hash = tunnel.popSent();
		Check.isEqual(hash.command, TunnelCommand.HASH);
		Check.isEqual(hash.args, Sha1.hash(payload));
	}

	@Test
	public void download() {
		MockTcpTunnel client = new MockTcpTunnel();
		MockTcpTunnel server = new MockTcpTunnel();
		client.link(server);

		String payload = "hello world!";
		TunnelVO chunk = new TunnelVO(TunnelCommand.CHUNK);
		chunk.setBody(payload);
		server.sendVO(chunk);

		String hash = Sha1.hash(payload);
		server.sendVO(new TunnelVO(TunnelCommand.HASH, hash));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		TcpChunks.download("/home", client, os);
		Check.isEqual(os.toString(), payload);
	}

	@Test
	public void downloadHashMismatch() {
		MockTcpTunnel client = new MockTcpTunnel();
		MockTcpTunnel server = new MockTcpTunnel();
		client.link(server);

		String payload = "hello world!";
		TunnelVO chunk = new TunnelVO(TunnelCommand.CHUNK);
		chunk.setBody(payload);
		server.sendVO(chunk);

		String hash = Sha1.hash("invalid");
		server.sendVO(new TunnelVO(TunnelCommand.HASH, hash));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BombSquad.diffuse(RuntimeIOException.class, "file corrupted", () -> {
			TcpChunks.download("/home", client, os);
		});
	}

	@Test
	public void pairedDownloadAndSendChunks() {
		MockTcpTunnel client = new MockTcpTunnel();
		MockTcpTunnel server = new MockTcpTunnel();
		client.link(server);

		String payload = "hello world!";
		TcpChunks.sendChunks(StreamTool.toInputStream(payload), server);

		ByteArrayOutputStream destination = new ByteArrayOutputStream();
		TcpChunks.download("/path", client, destination);
		Check.isEqual(destination.toString(), payload);
	}
}
