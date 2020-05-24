package com.eriklievaart.q.vfs;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.q.vfs.impl.UrlResolverService;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		addServiceWithCleanup(UrlResolver.class, new UrlResolverService());
	}
}