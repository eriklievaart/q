package com.eriklievaart.q.tcp.client;

import com.eriklievaart.q.tcp.vfs.TcpFileType;

public class TcpVO {

	public String name;
	public TcpFileType type;

	public TcpVO(String name, TcpFileType type) {
		this.name = name;
		this.type = type;
	}
}
