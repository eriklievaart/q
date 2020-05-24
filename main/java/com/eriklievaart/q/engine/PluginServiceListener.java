package com.eriklievaart.q.engine;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.osgi.OsgiSupplierFactory;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class PluginServiceListener implements SimpleServiceListener<QPlugin> {

	private OsgiSupplierFactory supplier;

	public PluginServiceListener(OsgiSupplierFactory supplier) {
		this.supplier = supplier;
	}

	@Override
	public void register(QPlugin service) {
		generateIndex();
	}

	@Override
	public void unregistering(QPlugin service) {
		generateIndex();
	}

	void generateIndex() {
		long start = System.currentTimeMillis();
		PluginIndex index = supplier.getEngineSupplierFactory().getPluginIndex();
		index.init(supplier.getServices(QPlugin.class), supplier.getEngineSupplierFactory());
		new LogTemplate(getClass()).debug("registering time: $ ms", System.currentTimeMillis() - start);
	}
}