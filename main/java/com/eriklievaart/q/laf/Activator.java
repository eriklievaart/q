package com.eriklievaart.q.laf;

import java.io.File;
import java.io.FileInputStream;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.q.api.QUi;

public class Activator extends ActivatorWrapper {

	@Override
	protected void init(BundleContext context) throws Exception {
		File root = new ContextWrapper(context).getBundleParentDir();
		File laf = new File(root, "data/ui/laf.txt");
		UiSettings settings = new UiSettings(new FileInputStream(laf));
		addWhiteboardWithCleanup(QUi.class, new LafListener(settings));
	}
}