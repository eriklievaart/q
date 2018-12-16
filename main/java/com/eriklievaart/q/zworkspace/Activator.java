package com.eriklievaart.q.zworkspace;

import java.io.File;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {
	@Override
	protected void init(BundleContext context) throws Exception {
		File workspaces = new ContextWrapper(context).getProjectFile("data/workspaces.txt");
		Supplier<QMainUi> supplier = () -> context.getService(context.getServiceReference(QMainUi.class));
		WorkspacePlugin service = new WorkspacePlugin(supplier, workspaces);

		addServiceWithCleanup(QPlugin.class, service);
		addServiceWithCleanup(QUi.class, service);
	}
}
