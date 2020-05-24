package com.eriklievaart.q.zvariable;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.api.Engine;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		ServiceCollection<Engine> engine = getServiceCollection(Engine.class);
		VariablePlugin service = new VariablePlugin(engine);
		addServiceWithCleanup(QPlugin.class, service);
	}
}