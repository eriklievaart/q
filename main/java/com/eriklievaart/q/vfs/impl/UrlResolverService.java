package com.eriklievaart.q.vfs.impl;

import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class UrlResolverService implements UrlResolver, SimpleServiceListener<ProtocolResolver> {

	private Map<String, ProtocolResolver> protocols = new Hashtable<>();

	@Override
	public VirtualFile resolve(String url) {
		return new ExactUrlResolver(protocols).resolve(url);
	}

	@Override
	public VirtualFile resolveFuzzy(VirtualFile base, String location) {
		return new FuzzyUrlResolver(new ExactUrlResolver(protocols)).resolve(base, location);
	}

	@Override
	public void register(ProtocolResolver protocol) {
		protocols.put(protocol.getProtocol(), protocol);
	}

	@Override
	public void unregistering(ProtocolResolver protocol) {
		protocols.remove(protocol.getProtocol());
	}
}