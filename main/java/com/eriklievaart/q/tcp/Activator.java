package com.eriklievaart.q.tcp;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.vfs.api.ProtocolResolver;

public class Activator extends ActivatorWrapper {

	private TcpDependencies dependencies;

	@Override
	protected void init(BundleContext context) throws Exception {
		dependencies = new TcpDependencies(getContextWrapper());

		addServiceWithCleanup(ProtocolResolver.class, dependencies.protocols);
		addServiceWithCleanup(QUi.class, dependencies.service);
		dependencies.server.start();
	}

	@Override
	protected void shutdown() throws Exception {
		dependencies.server.shutdown();
	}
}
