package com.eriklievaart.q.zfind;

import java.util.function.Supplier;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		try {
			Supplier<QMainUi> uiSupplier = () -> context.getService(context.getServiceReference(QMainUi.class));
			Supplier<Engine> engineSupplier = () -> context.getService(context.getServiceReference(Engine.class));
			FindService service = new FindService(uiSupplier, engineSupplier);

			addServiceWithCleanup(QPlugin.class, service);
			addServiceWithCleanup(QUi.class, service);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
