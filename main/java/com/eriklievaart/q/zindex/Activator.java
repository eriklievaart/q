package com.eriklievaart.q.zindex;

import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;

public class Activator extends ActivatorWrapper {
	@Override
	public void init(BundleContext context) {
		Supplier<QMainUi> ui = () -> context.getService(context.getServiceReference(QMainUi.class));
		Supplier<UrlResolver> resolver = () -> context.getService(context.getServiceReference(UrlResolver.class));
		addServiceWithCleanup(QPlugin.class, new IndexPlugin(ui, resolver));
	}
}
