package com.eriklievaart.q.tcp.client;

import java.io.File;
import java.util.List;

import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class TcpHosts {

	private List<String> hosts = NewCollection.list();
	private File file;

	public TcpHosts(File file) {
		this.file = file;
		load();
	}

	public String getMostRecent() {
		return hosts.isEmpty() ? "127.0.0.1" : hosts.get(0);
	}

	public void add(String address) {
		hosts.remove(address);
		hosts.add(0, address);
		store();
	}

	private void store() {
		FileTool.writeLines(file, hosts);
	}

	private void load() {
		if (file.isFile()) {
			hosts = FileTool.readLines(file);
		}
	}
}
