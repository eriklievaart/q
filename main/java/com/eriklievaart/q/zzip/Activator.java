package com.eriklievaart.q.zzip;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {
	@Override
	protected void init(BundleContext context) throws Exception {
		ServiceCollection<QMainUi> qui = getServiceCollection(QMainUi.class);
		addServiceWithCleanup(QPlugin.class, new ZipService(qui));
	}
}
