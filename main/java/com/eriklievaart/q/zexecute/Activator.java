package com.eriklievaart.q.zexecute;

import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		Supplier<QMainUi> ui = () -> context.getService(context.getServiceReference(QMainUi.class));
		Supplier<Engine> engine = () -> context.getService(context.getServiceReference(Engine.class));
		ExecuteService service = new ExecuteService(ui, engine);

		addServiceWithCleanup(QUi.class, service);
		addServiceWithCleanup(QPlugin.class, service);
	}
}