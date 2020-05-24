package com.eriklievaart.q.customize;

import java.io.File;

import org.osgi.framework.BundleContext;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.ResourceTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class Activator extends ActivatorWrapper {
	private LogTemplate log = new LogTemplate(getClass());

	@Override
	protected void init(BundleContext context) throws Exception {
		File file = new File(new ContextWrapper(context).getBundleParentDir(), "customize.txt");
		if (!file.exists()) {
			log.info("missing file %, creating example file");
			FileTool.writeStringToFile(ResourceTool.getString(Activator.class, "/customize/example.txt"), file);
		}
		ServiceCollection<Engine> engine = getServiceCollection(Engine.class);
		CustomizeService service = new CustomizeService(new CustomizeConfig(file, engine));
		addServiceWithCleanup(QUi.class, service);
	}
}