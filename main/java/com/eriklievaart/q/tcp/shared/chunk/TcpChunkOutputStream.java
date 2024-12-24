package com.eriklievaart.q.tcp.shared.chunk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;

import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.tunnel.TcpTunnel;
import com.eriklievaart.toolkit.io.api.sha1.Sha1Digest;

public class TcpChunkOutputStream extends OutputStream {

	int index = 0;
	private Lock lock;
	private TcpTunnel tunnel;
	Sha1Digest digest = new Sha1Digest();
	private byte[] buffer = new byte[ChunkIterator.BUFFER_SIZE];

	public TcpChunkOutputStream(TcpTunnel tunnel, Lock lock) {
		this.tunnel = tunnel;
		this.lock = lock;
	}

	@Override
	public void write(int b) throws IOException {
		buffer[index++] = (byte) b;
		if (index == buffer.length) {
			sendChunk(index);
			index = 0;
		}
	}

	private void sendChunk(int bytes) {
		digest.update(buffer, 0, bytes);
		TunnelVO chunk = new TunnelVO(TunnelCommand.CHUNK);
		chunk.buffer = buffer;
		chunk.bytes = bytes;
		tunnel.sendVO(chunk);
	}

	@Override
	public void flush() throws IOException {
		if (index > 0) {
			sendChunk(index);
			index = 0;
		}
		super.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			flush();
			buffer = null;
			tunnel.sendVO(new TunnelVO(TunnelCommand.HASH, digest.calculateHash()));

		} finally {
			lock.unlock();
		}
	}
}
