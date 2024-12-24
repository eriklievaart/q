package com.eriklievaart.q.tcp.shared.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.tunnel.TcpTunnel;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.sha1.Sha1Digest;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class TcpChunks {

	public static void download(String path, TcpTunnel tunnel, OutputStream destination) {
		tunnel.sendVO(new TunnelVO(TunnelCommand.TO_CLIENT, path));
		downloadChunks(tunnel, destination);
	}

	public static void downloadChunks(TcpTunnel tunnel, OutputStream destination) {
		try {
			Sha1Digest digest = new Sha1Digest();

			TunnelVO vo = tunnel.receiveVO();
			while (vo.isCommand(TunnelCommand.CHUNK)) {
				vo.writeBodyTo(destination);
				vo.update(digest);
				vo = tunnel.receiveVO();
			}
			RuntimeIOException.unless(vo.isCommand(TunnelCommand.HASH), "Expecting HASH, but received $", vo.command);

			String actual = digest.calculateHash();
			String expected = vo.args.trim();
			RuntimeIOException.unless(Str.isEqual(actual, expected), "file corrupted! $ != $", actual, expected);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public static void sendChunks(InputStream is, TcpTunnel tunnel) {
		try {
			Sha1Digest digest = new Sha1Digest();
			ChunkIterator iterator = new ChunkIterator(is);

			while (iterator.hasNext()) {
				TunnelVO vo = new TunnelVO(TunnelCommand.CHUNK);
				vo.setBody(iterator.next());
				vo.update(digest);
				tunnel.sendVO(vo);
			}
			tunnel.sendVO(new TunnelVO(TunnelCommand.HASH, digest.calculateHash()));

		} catch (IOException e) {
			tunnel.sendVO(new TunnelVO(TunnelCommand.ERROR, e.getMessage()));
			throw new RuntimeIOException(e);
		}
	}
}