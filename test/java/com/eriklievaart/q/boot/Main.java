package com.eriklievaart.q.boot;

import java.io.File;
import java.util.List;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.zcopy.CopyService;
import com.eriklievaart.q.zdelete.DeleteService;
import com.eriklievaart.q.zexecute.ExecuteService;
import com.eriklievaart.q.zindex.IndexService;
import com.eriklievaart.q.zlocation.LocationService;
import com.eriklievaart.q.zmove.MoveService;
import com.eriklievaart.q.znew.NewService;
import com.eriklievaart.q.zsize.SizeService;
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
		services.add(new NewService());
		services.add(new CopyService());
		services.add(new MoveService());
		services.add(new ExecuteService(esf.getMainUiSupplier()));
		services.add(new LocationService(esf.getMainUiSupplier()));
		services.add(new DeleteService());
		services.add(new SizeService(esf.getMainUiSupplier()));
		services.add(new IndexService(esf.getMainUiSupplier(), esf.getUrlResolverSupplier()));
		services.add(desf.getFindService());
		esf.getPluginIndex().init(services, esf);
		return desf;
	}
}
