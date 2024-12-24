package com.eriklievaart.q.tcp.client;

import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.eriklievaart.toolkit.io.api.LineFilter;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class TcpHosts {

	private AtomicReference<String> active = new AtomicReference<>();
	private Map<String, String> hosts = NewCollection.orderedMap();
	private File file;

	public TcpHosts(File file) {
		this.file = file;
		load();
	}

	public String getMostRecent() {
		return hosts.keySet().stream().reduce((a, b) -> b).orElse("127.0.0.1");
	}

	public void add(String address) {
		String value = hosts.remove(address);
		hosts.put(address, value);
		store();
	}

	private void store() {
		PropertiesIO.storeStrings(hosts, file);
	}

	private void load() {
		if (file.isFile()) {
			for (String line : new LineFilter(file).dropBlank().dropHash().list()) {
				if (line.contains("=")) {
					String[] hostToDirectory = line.split("=");
					hosts.put(hostToDirectory[0], hostToDirectory.length > 1 ? hostToDirectory[1] : "");
				} else {
					hosts.put(line, "");
				}
			}
		}
	}

	public void setActive(String ip) {
		Check.notBlank(ip);
		active.set(ip);
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
