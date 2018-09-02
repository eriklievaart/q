package com.eriklievaart.q.zindex;

import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> registration;

	@Override
	public void start(BundleContext context) throws Exception {
		Supplier<QMainUi> ui = () -> context.getService(context.getServiceReference(QMainUi.class));
		Supplier<UrlResolver> resolver = () -> context.getService(context.getServiceReference(UrlResolver.class));
		registration = context.registerService(QPlugin.class, new IndexService(ui, resolver), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
