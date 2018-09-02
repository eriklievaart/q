package com.eriklievaart.q.zsize;

import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> pluginRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		Supplier<QMainUi> supplier = () -> context.getService(context.getServiceReference(QMainUi.class));
		SizeService service = new SizeService(supplier);
		pluginRegistration = context.registerService(QPlugin.class, service, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		pluginRegistration.unregister();
	}
}
