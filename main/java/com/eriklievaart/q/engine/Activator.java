package com.eriklievaart.q.engine;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.engine.osgi.OsgiSupplierFactory;

public class Activator implements BundleActivator {

	private PluginServiceListener listener;
	private ServiceRegistration<Engine> engineRegistration;
	private ServiceRegistration<QUi> uiRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		OsgiSupplierFactory supplier = new OsgiSupplierFactory(context);
		EngineService engine = new EngineService(supplier.getEngineSupplierFactory());

		listener = new PluginServiceListener(supplier);
		context.addServiceListener(listener);
		listener.generateIndex();

		engineRegistration = context.registerService(Engine.class, engine, null);
		uiRegistration = context.registerService(QUi.class, engine, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		engineRegistration.unregister();
		uiRegistration.unregister();
		context.removeServiceListener(listener);
	}
}