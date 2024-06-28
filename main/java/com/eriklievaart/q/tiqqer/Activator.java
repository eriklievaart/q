package com.eriklievaart.q.tiqqer;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.tiqqer.swing.api.TiqqerFrame;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		ServiceCollection<TiqqerFrame> frame = getContextWrapper().getServiceCollection(TiqqerFrame.class);
		addServiceWithCleanup(QUi.class, new TiqqerService(frame));
	}
}
