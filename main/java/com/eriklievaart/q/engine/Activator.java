package com.eriklievaart.q.engine;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.engine.osgi.OsgiSupplierFactory;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		OsgiSupplierFactory supplier = new OsgiSupplierFactory(context);
		EngineService engine = new EngineService(supplier.getEngineSupplierFactory());

		PluginServiceListener listener = new PluginServiceListener(supplier);
		addServiceListenerWithCleanup(QPlugin.class, listener);
		listener.generateIndex();

		addServiceWithCleanup(Engine.class, engine);
		addServiceWithCleanup(QUi.class, engine);
	}
}