package com.eriklievaart.q.boot;

import java.io.File;
import java.util.List;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.zcopy.CopyService;
import com.eriklievaart.q.zdelete.DeleteService;
import com.eriklievaart.q.zexecute.ExecuteService;
import com.eriklievaart.q.zindex.IndexPlugin;
import com.eriklievaart.q.zlocation.LocationPlugin;
import com.eriklievaart.q.zmove.MovePlugin;
import com.eriklievaart.q.znew.NewPlugin;
import com.eriklievaart.q.zsize.SizePlugin;
import com.eriklievaart.q.zworkspace.WorkspacePlugin;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.WindowSaver;

public class Main {

	public static void main(String[] args) {
		WindowSaver.initialize(new File("/tmp/q/window-saver.ini"));
		long start = System.currentTimeMillis();

		wireApplication().getContextFromUi().show();

		LogTemplate log = new LogTemplate(Main.class);
		log.info("startup time $ ms", System.currentTimeMillis() - start);
	}

	public static DummyBeanFactory wireApplication() {
		DummyBeanFactory desf = new DummyBeanFactory();
		EngineSupplierFactory esf = desf.getEngineSupplierFactory();

		List<QPlugin> services = NewCollection.list();
		services.add(new NewPlugin());
		services.add(new CopyService());
		services.add(new MovePlugin());
		services.add(new ExecuteService(esf.getMainUiSupplier()));
		services.add(new LocationPlugin(esf.getMainUiSupplier()));
		services.add(new DeleteService());
		services.add(new SizePlugin(esf.getMainUiSupplier(), desf.getEngineSupplier()));
		services.add(new IndexPlugin(esf.getMainUiSupplier(), esf.getUrlResolverSupplier()));
		services.add(new WorkspacePlugin(esf.getMainUiSupplier(), new File("/tmp/q/workspaces.txt")));
		services.add(desf.getFindService());
		esf.getPluginIndex().init(services, esf);

		return desf;
	}
}
