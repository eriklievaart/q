package com.eriklievaart.q.zzip;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;

public class Activator extends ActivatorWrapper {
	@Override
	protected void init(BundleContext context) throws Exception {
		addServiceWithCleanup(QPlugin.class, new ZipService());
	}
}
