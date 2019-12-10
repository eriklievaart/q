package com.eriklievaart.q.bind;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.q.bind.registry.ComponentBinder;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class Activator implements BundleActivator {

	private final LogTemplate log = new LogTemplate(getClass());
	private final ComponentBinder components = new ComponentBinder();
	private final BindServiceListener listener = new BindServiceListener(components);

	@Override
	public void start(BundleContext context) throws Exception {
		log.info("init bundle q-bind");

		components.setMainUiSupplier(() -> getUi(context));
		listener.setBundleContext(context);
		context.addServiceListener(listener);

		components.bindAll(listener.getAllServices());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		components.unbindAll();
		context.removeServiceListener(listener);
	}

	private QMainUi getUi(BundleContext context) {
		ServiceReference<QMainUi> reference = context.getServiceReference(QMainUi.class);
		return reference == null ? null : context.getService(reference);
	}
}