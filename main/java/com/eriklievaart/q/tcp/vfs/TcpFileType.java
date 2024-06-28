package com.eriklievaart.q.tcp.vfs;

import com.eriklievaart.toolkit.lang.api.FormattedException;

public enum TcpFileType {

	DIRECTORY, FILE, MISSING;

	public static TcpFileType from(String trim) {
		for (TcpFileType type : values()) {
			if (type.getShortForm().equalsIgnoreCase(trim)) {
				return type;
			}
		}
		throw new FormattedException("unknown TcpFileType: %", trim);
	}

	public String getShortForm() {
		return "" + name().charAt(0);
	}
}
