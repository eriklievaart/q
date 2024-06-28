package com.eriklievaart.q.vfs;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.Whiteboard;
import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.q.vfs.impl.UrlResolverService;
import com.eriklievaart.q.vfs.protocol.FileProtocolResolver;
import com.eriklievaart.q.vfs.protocol.MemoryProtocolResolver;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		UrlResolverService resolver = new UrlResolverService();
		addServiceWithCleanup(UrlResolver.class, resolver);

		Whiteboard<ProtocolResolver> whiteboard = addWhiteboardWithCleanup(ProtocolResolver.class);
		whiteboard.addListener(resolver);

		addServiceWithCleanup(ProtocolResolver.class, new MemoryProtocolResolver());
		addServiceWithCleanup(ProtocolResolver.class, new FileProtocolResolver());
	}
}