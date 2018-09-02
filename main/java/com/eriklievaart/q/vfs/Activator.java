package com.eriklievaart.q.vfs;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.q.vfs.impl.UrlResolverService;

public class Activator implements BundleActivator {

	private ServiceRegistration<UrlResolver> registration;

	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(UrlResolver.class, new UrlResolverService(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
