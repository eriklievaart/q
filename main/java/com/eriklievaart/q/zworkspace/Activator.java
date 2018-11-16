package com.eriklievaart.q.zworkspace;

import java.io.File;
import java.util.function.Supplier;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.osgi.toolkit.api.BundleWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator implements BundleActivator {

	private ServiceRegistration<QPlugin> pluginRegistration;
	private ServiceRegistration<QUi> uiRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		File workspaces = new BundleWrapper(context).getProjectFile("data/workspaces.txt");
		Supplier<QMainUi> supplier = () -> context.getService(context.getServiceReference(QMainUi.class));
		WorkspacePlugin service = new WorkspacePlugin(supplier, workspaces);

		pluginRegistration = context.registerService(QPlugin.class, service, null);
		uiRegistration = context.registerService(QUi.class, service, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		pluginRegistration.unregister();
		uiRegistration.unregister();
	}

}
