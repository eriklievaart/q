package com.eriklievaart.q.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.logging.LogRecord;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.osgi.toolkit.api.ActivatorWrapper;
import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.Formatter;
import com.eriklievaart.toolkit.logging.api.LogConfig;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.logging.api.appender.SimpleFileAppender;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;

public class Activator extends ActivatorWrapper {
	LogTemplate log = new LogTemplate(getClass());

	private ShutdownListener shutdown;

	@Override
	protected void init(BundleContext context) throws Exception {
		UiResourcePaths files = new UiResourcePaths(new ContextWrapper(context).getBundleParentDir());

		initWindowSaver(files.getWindowSaverConfig());
		UiBeanFactory beans = new UiBeanFactory(files, () -> getEngineService(context), () -> getUrlResolver(context));

		UiService service = new UiService(beans);
		addServiceWithCleanup(QUi.class, service);
		addServiceWithCleanup(QMainUi.class, service);

		configureFileOperationLogFile(files.getFileOperationLog());
		shutdown = new ShutdownListener(context, beans);
		beans.getController().registerShutdownListener(shutdown);

		SwingThread.invokeLater(() -> {
			service.showFrame();
		});
	}

	@Override
	protected void shutdown() throws Exception {
		shutdown.setOsgiFrameworkShutdown(false);
		WindowSaver.shutdown();
	}

	private void configureFileOperationLogFile(File file) throws FileNotFoundException {
		SimpleFileAppender appender = new SimpleFileAppender(file);
		appender.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {
				return record.getMessage();
			}
		});
		LogConfig.setAppenders("virtual.files", Arrays.asList(appender));
	}

	public Engine getEngineService(BundleContext context) {
		ServiceReference<Engine> reference = context.getServiceReference(Engine.class);
		Check.notNull(reference, "Service not found: " + Engine.class);
		return context.getService(reference);
	}

	public UrlResolver getUrlResolver(BundleContext context) {
		ServiceReference<UrlResolver> reference = context.getServiceReference(UrlResolver.class);
		Check.notNull(reference, "Service not found: " + UrlResolver.class);
		return context.getService(reference);
	}

	private void initWindowSaver(File configFile) {
		try {
			WindowSaver.shutdown();
			WindowSaver.initialize(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}