package com.eriklievaart.q.tcp.shared;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.eriklievaart.toolkit.io.api.sha1.Sha1Digest;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class TunnelVO {

	public TunnelCommand command;
	public String args;
	public byte[] buffer = new byte[0];
	public int bytes = 0;

	public TunnelVO(TunnelCommand command) {
		this.command = command;
	}

	public TunnelVO(TunnelCommand command, String args) {
		this.command = command;
		this.args = args;
	}

	public String getCommandLine() {
		return Str.isBlank(args) ? command.toString() : command + " " + args;
	}

	public boolean isCommand(TunnelCommand expected) {
		return command == expected;
	}

	public boolean getArgsAsBoolean() {
		return Str.isEqual(Str.trim(args), "true");
	}

	public int getArgsAsInteger() {
		Check.matches(args, "\\d++");
		return Integer.parseInt(args.trim());
	}

	public void setBody(List<String> lines) {
		setBody(Str.joinLines(lines));
	}

	public void setBody(String body) {
		setBody(body.getBytes());
	}

	public void setBody(byte[] value) {
		buffer = value;
		bytes = value.length;
	}

	public String getBodyAsString() {
		return new String(buffer);
	}

	public String[] getBodyAsLines() {
		return bytes == 0 ? new String[0] : Str.splitLines(getBodyAsString());
	}

	public void writeBodyTo(OutputStream destination) throws IOException {
		destination.write(buffer, 0, bytes);
	}

	public void update(Sha1Digest digest) {
		digest.update(buffer, 0, bytes);
	}

	@Override
	public String toString() {
		if (args == null) {
			return ToString.simple(this, "$[$]", command);
		}
		return ToString.simple(this, "$[$ $]", command, args);
	}
}
