package com.eriklievaart.q.bind;

import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.registry.ComponentBinder;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {

	private final ComponentBinder components = new ComponentBinder();
	private final BindServiceListener listener = new BindServiceListener(components);

	@Override
	protected void init(BundleContext context) throws Exception {
		registerUserBindings(context);

		components.setMainUiSupplier(() -> getMainUi(context));
		listener.setBundleContext(context);
		context.addServiceListener(listener);

		components.bindAll(listener.getAllServices());
	}

	private void registerUserBindings(BundleContext context) {
		BindingPath path = new BindingPath();
		Supplier<Engine> engine = () -> context.getService(context.getServiceReference(Engine.class));
		BindService service = new BindService(path.getActionFile(), path.getBindingFile(), engine);

		if (service.isConfigured()) {
			addServiceWithCleanup(QUi.class, service);
		}
	}

	private QMainUi getMainUi(BundleContext context) {
		ServiceReference<QMainUi> reference = context.getServiceReference(QMainUi.class);
		return reference == null ? null : context.getService(reference);
	}
}
