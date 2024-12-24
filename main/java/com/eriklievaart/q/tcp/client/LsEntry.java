package com.eriklievaart.q.tcp.client;

import com.eriklievaart.q.tcp.vfs.TcpFileType;

public class LsEntry {

	public String name;
	public TcpFileType type;

	public LsEntry(String name, TcpFileType type) {
		this.name = name;
		this.type = type;
	}
}
