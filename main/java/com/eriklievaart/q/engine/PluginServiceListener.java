package com.eriklievaart.q.engine;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.osgi.OsgiSupplierFactory;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class PluginServiceListener implements ServiceListener {

	private OsgiSupplierFactory supplier;

	public PluginServiceListener(OsgiSupplierFactory supplier) {
		this.supplier = supplier;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		ServiceReference<?> reference = event.getServiceReference();
		Object service = supplier.getService(reference);
		if (service instanceof QPlugin) {
			long start = System.currentTimeMillis();
			if (event.getType() == ServiceEvent.REGISTERED) {
				generateIndex();
				new LogTemplate(getClass()).debug("registering time: $ ms", System.currentTimeMillis() - start);
			}
			if (event.getType() == ServiceEvent.UNREGISTERING) {
				generateIndex();
			}
		}
	}

	void generateIndex() {
		PluginIndex index = supplier.getEngineSupplierFactory().getPluginIndex();
		index.init(supplier.getServices(QPlugin.class), supplier.getEngineSupplierFactory());
	}
}
