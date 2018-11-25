package com.eriklievaart.q.zexecute;

import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		Supplier<QMainUi> supplier = () -> context.getService(context.getServiceReference(QMainUi.class));
		addServiceWithCleanup(QPlugin.class, new ExecuteService(supplier));
	}
}
