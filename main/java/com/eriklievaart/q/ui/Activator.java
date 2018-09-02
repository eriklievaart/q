package com.eriklievaart.q.ui;

import java.io.File;
import java.util.Arrays;
import java.util.logging.LogRecord;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.Formatter;
import com.eriklievaart.toolkit.logging.api.LogConfig;
import com.eriklievaart.toolkit.logging.api.appender.SimpleFileAppender;
import com.eriklievaart.toolkit.swing.api.WindowSaver;

public class Activator implements BundleActivator {

	private ServiceRegistration<?> registration;
	private ShutdownListener shutdown;
	private UiService service;

	@Override
	public void start(BundleContext context) throws Exception {
		UiResourcePaths files = getRootDir(context);
		initWindowSaver(files.getWindowSaverConfig());
		UiBeanFactory beans = new UiBeanFactory(files, () -> getEngineService(context), () -> getUrlResolver(context));

		service = new UiService(beans);
		registration = context.registerService(getServiceClasses(), service, null);
		configureLogFile(files.getFileOperationLog());

		service.showFrame();
		shutdown = new ShutdownListener(context, beans);
		beans.getController().registerShutdownListener(shutdown);
	}

	private String[] getServiceClasses() {
		return new String[] { QUi.class.getName(), QMainUi.class.getName() };
	}

	private UiResourcePaths getRootDir(BundleContext context) {
		String location = context.getBundle().getLocation();
		String parent = UrlTool.getParent(UrlTool.getParent(UrlTool.getPath(location)));
		return new UiResourcePaths(new File(parent));
	}

	private void configureLogFile(File file) {
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

	@Override
	public void stop(BundleContext context) throws Exception {
		shutdown.setOsgiFrameworkShutdown(false);
		service.shutdown();
		registration.unregister();
		WindowSaver.shutdown();
	}
}
