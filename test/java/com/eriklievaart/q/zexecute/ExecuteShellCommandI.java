package com.eriklievaart.q.zexecute;

import java.util.Arrays;

import org.junit.Test;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.engine.EngineService;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QContextFactory;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class ExecuteShellCommandI extends SandboxTest {

	@Test
	public void invoke() throws Exception {
		SystemFile file = createFile("hup/holland");
		String dir = file.getParentFile().get().getPath();
		QContext context = new QContextFactory().leftSystemDir(dir).make();
		ExecuteService service = new ExecuteService(() -> new DummyQMainUi()) {
			@Override
			public ThreadPolicy getThreadPolicy() {
				return ThreadPolicy.CURRENT;
			}
		};
		invokeOnEngine(service, context, "e -s $dir|touch oranje");
		checkIsFile("hup/oranje");
	}

	private void invokeOnEngine(QPlugin plugin, QContext context, String raw) {
		EngineSupplierFactory factory = new DummyBeanFactory().context(context).getEngineSupplierFactory();
		factory.getPluginIndex().init(Arrays.asList(plugin), factory);
		new EngineService(factory).invoke(raw);
	}
}
