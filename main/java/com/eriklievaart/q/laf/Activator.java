package com.eriklievaart.q.laf;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.toolkit.io.api.ResourceTool;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		UiSettings settings = new UiSettings(ResourceTool.getInputStream(getClass(), "/laf/laf.txt"));
		addWhiteboardWithCleanup(QUi.class, new LafListener(settings));
	}
}