package com.eriklievaart.q.zfind;

import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> pluginRegistration;
	private ServiceRegistration<QUi> uiRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		try {
			Supplier<QMainUi> uiSupplier = () -> context.getService(context.getServiceReference(QMainUi.class));
			Supplier<Engine> engineSupplier = () -> context.getService(context.getServiceReference(Engine.class));

			FindService service = new FindService(uiSupplier, engineSupplier);
			pluginRegistration = context.registerService(QPlugin.class, service, null);
			uiRegistration = context.registerService(QUi.class, service, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		pluginRegistration.unregister();
		uiRegistration.unregister();
	}
}
