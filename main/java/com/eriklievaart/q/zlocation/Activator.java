package com.eriklievaart.q.zlocation;

import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> registration;

	@Override
	public void start(BundleContext context) throws Exception {
		Supplier<QMainUi> supplier = () -> context.getService(context.getServiceReference(QMainUi.class));
		registration = context.registerService(QPlugin.class, new LocationPlugin(supplier), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
