package com.eriklievaart.q.tcp.shared.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ChunkIterator {
	public static final int BUFFER_SIZE = 1024 * 1024;

	private InputStream is;
	private byte[] buffer;

	public ChunkIterator(InputStream is) throws IOException {
		this.is = is;
		prepareNextChunk();
	}

	public boolean hasNext() {
		return buffer.length > 0;
	}

	public byte[] next() throws IOException {
		byte[] result = buffer;
		prepareNextChunk();
		return result;
	}

	public String nextString() throws IOException {
		return new String(next());
	}

	private void prepareNextChunk() throws IOException {
		buffer = new byte[BUFFER_SIZE];

		int total = 0;
		while (total < BUFFER_SIZE) {
			int read = is.read(buffer, total, BUFFER_SIZE - total);
			if (read == -1) {
				break;
			}
			total += read;
		}
		if (total < BUFFER_SIZE) {
			buffer = Arrays.copyOf(buffer, total);
		}
	}
}
