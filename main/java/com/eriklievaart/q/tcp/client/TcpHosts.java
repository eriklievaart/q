package com.eriklievaart.q.tcp.client;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class TcpHosts {

	private AtomicReference<String> active = new AtomicReference<>();
	private Map<String, String> hosts = NewCollection.map();
	private File file;

	public TcpHosts(File file) {
		this.file = file;
		load();
	}

	public String getMostRecent() {
		return active.get();
	}

	public void add(String address) {
		Check.notNull(address);
		hosts.putIfAbsent(address, "");
		store();
	}

	private void store() {
		PropertiesIO.storeStrings(hosts, file);
	}

	private void load() {
		if (!file.isFile()) {
			return;
		}
		hosts.putAll(PropertiesIO.loadStrings(file));
		if (hosts.containsKey("active")) {
			active.set(hosts.get("active"));
		}
	}

	public void setActive(String ip) {
		Check.notBlank(ip);
		hosts.put("active", ip);
		active.set(ip);
		store();
	}

	public String getMostRecentDirectory(String ip) {
		return hosts.getOrDefault(ip, "");
	}

	public void setMostRecentDirectory(String path) {
		Check.notBlank(active.get());
		if (Str.notEqual(hosts.get(active.get()), path)) {
			hosts.put(active.get(), path);
			store();
		}
	}
}
