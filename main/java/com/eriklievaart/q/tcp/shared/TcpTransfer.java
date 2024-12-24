package com.eriklievaart.q.tcp.shared;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.sha1.Sha1Digest;
import com.eriklievaart.toolkit.io.api.sha1.Sha1InputStream;
import com.eriklievaart.toolkit.io.api.sha1.Sha1OutputStream;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class TcpTransfer {
	private static final int MEGABYTE = 1024 * 1024;

	private LogTemplate log = new LogTemplate(getClass());

	private InputStream is;
	private OutputStream os;

	public TcpTransfer(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	public static TcpTransfer from(Socket socket) {
		try {
			return new TcpTransfer(socket.getInputStream(), socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void writeBoolean(boolean value) {
		try {
			os.write(value ? 1 : 0);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public boolean readBoolean() {
		try {
			return is.read() == 1;
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void writeString(String format, Object... args) {
		writeString(Str.sub(format, args));
	}

	public void writeString(String message) {
		log.trace("str >> $", message);
		writeStringSilent(message);
	}

	private void writeStringSilent(String message) {
		try {
			byte[] bytes = message.getBytes();
			os.write(bytes.length);
			os.write(bytes);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public String readString() {
		String message = readStringSilent();
		log.trace("str << $", message);
		return message;
	}

	private String readStringSilent() {
		try {
			int len = is.read();
			TcpDisconnectException.on(len == -1);

			byte[] bytes = new byte[len];
			is.read(bytes);
			return new String(bytes);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public List<String> readLines() {
		int lines = readIntSilent();
		List<String> result = NewCollection.list();

		for (int i = 0; i < lines; i++) {
			result.add(readStringSilent());
		}
		log.trace("[$] << (...)", lines);
		return result;
	}

	public void writeLines(List<String> lines) {
		log.trace("[$] >> (...)", lines.size());
		writeIntSilent(lines.size());
		for (String line : lines) {
			writeStringSilent(line);
		}
	}

	public void writeInt(int value) {
		log.trace("int >> $", value);
		writeIntSilent(value);
	}

	private void writeIntSilent(int value) {
		try {
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.putInt(value);
			os.write(bb.array(), 0, 4);

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public int readInt() {
		int value = readIntSilent();
		log.trace("int << $", value);
		return value;

	}

	private int readIntSilent() {
		try {
			byte[] bytes = new byte[4];
			is.read(bytes);
			int value = new BigInteger(bytes).intValue();
			return value;
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void writeBytes(byte[] buffer, int length) {
		try {
			os.write(buffer, 0, length);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public byte[] readBytes(int length) {
		Check.isTrue(length <= MEGABYTE, "chunk is too large! $ bytes", length);
		byte[] buffer = new byte[length];

		try {
			int progress = 0;
			while (progress < length) {
				progress += is.read(buffer, progress, length - progress);
			}
			return buffer;

		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	public void download(Sha1OutputStream destination) throws IOException {
		byte[] chunk = new byte[MEGABYTE];

		int serverChunk = readIntSilent();
		while (serverChunk != -1) {
			int transferred = 0;
			while (transferred < serverChunk) {
				int chunkSize = is.read(chunk, 0, serverChunk - transferred);
				destination.write(chunk, 0, chunkSize);
				transferred += chunkSize;
			}
			serverChunk = readIntSilent();
		}
		if (!readString().equals(destination.getHash())) {
			throw new IOException("file corrupted in transfer!");
		}
	}

	public OutputStream createOutputStream(Runnable onclose) {
		return new OutputStream() {
			private Buffer buffer = new Buffer();
			private Sha1Digest digest = new Sha1Digest();

			@Override
			public void write(int b) throws IOException {
				byte chopped = (byte) b;
				buffer.append(chopped);
				digest.update(chopped);
				if (buffer.isFull()) {
					buffer.writeBack();
				}
			}

			@Override
			public void close() throws IOException {
				buffer.writeBack();
				writeIntSilent(-1);
				writeString(digest.calculateHash());
				log.debug("closing OutputStream; upload complete!");
				onclose.run();
				super.close();
			}
		};
	}

	public void upload(Sha1InputStream data) {
		try {
			byte[] chunk = new byte[MEGABYTE];

			while (true) {
				int len = data.read(chunk);
				writeIntSilent(len);
				if (len == -1) {
					writeString(data.getHash());
					return;
				}
				os.write(chunk, 0, len);
			}
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}

	private class Buffer {
		int index = 0;
		byte[] chunk = new byte[MEGABYTE];

		public void append(byte b) {
			chunk[index++] = b;
		}

		public boolean isFull() {
			return index == chunk.length;
		}

		public void writeBack() throws IOException {
			writeIntSilent(index);
			os.write(chunk, 0, index);
			index = 0;
		}
	}
}
