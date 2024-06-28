package com.eriklievaart.q.vfs.impl;

import java.util.Map;
import java.util.Optional;

import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class ExactUrlResolver {

	private Map<String, ProtocolResolver> protocols;

	public ExactUrlResolver(Map<String, ProtocolResolver> protocols) {
		this.protocols = protocols;
	}

	public VirtualFile resolve(String url) {
		Optional<String> option = UrlTool.getProtocol(url);

		if (option.isPresent()) {
			String protocol = option.get();
			CheckCollection.isPresent(protocols, protocol, "unknown protocol %", protocol);
			return protocols.get(protocol).resolve(UrlTool.getPath(url));
		}
		return new SystemFile(UrlTool.getPath(url));
	}
}