package com.eriklievaart.q.zdelete;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QPlugin;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> registration;

	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(QPlugin.class, new DeleteService(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
