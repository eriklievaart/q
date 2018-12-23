package com.eriklievaart.q.engine.osgi;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.registry.ComponentBinder;
import com.eriklievaart.q.engine.EngineService;
import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.UiService;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QContextFactory;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.q.vfs.impl.UrlResolverService;
import com.eriklievaart.q.zfind.FindService;

public class DummyBeanFactory {

	private AtomicReference<UiService> uiReference = new AtomicReference<>();
	private AtomicReference<Engine> engineReference = new AtomicReference<>();

	private Supplier<QMainUi> ui = () -> getUiBean();

	private UrlResolverService resolver = new UrlResolverService();
	private QContext initialContext = new QContextFactory().make();
	private EngineSupplierFactory factory = new EngineSupplierFactory(() -> resolver, () -> initialContext, ui);
	private EngineService engine = new EngineService(factory);
	private FindService find = new FindService(ui, () -> engine);
	private UiResourcePaths resources = new UiResourcePaths(new File("/tmp/q"));

	public DummyBeanFactory() {
		engineReference.set(engine);
	}

	private QMainUi getUiBean() {
		initUi();
		return uiReference.get();
	}

	private void initUi() {
		if (uiReference.get() != null) {
			return;
		}
		uiReference.set(new UiService(new UiBeanFactory(resources, () -> engineReference.get(), () -> resolver)));

		Map<Long, QUi> bindings = new Hashtable<>();
		bindings.put(1l, (UiService) factory.getMainUiSupplier().get());
		bindings.put(2l, engine);
		bindings.put(3l, find);
		ComponentBinder binder = new ComponentBinder();
		binder.setMainUiSupplier(factory.getMainUiSupplier());
		binder.bindAll(bindings);
	}

	public FindService getFindService() {
		return find;
	}

	public DummyBeanFactory getContextFromUi() {
		factory.setQContextSupplier(() -> ui.get().getQContext());
		return this;
	}

	public DummyBeanFactory context(QContext value) {
		factory.setQContextSupplier(() -> value);
		return this;
	}

	public DummyBeanFactory context(Supplier<QContext> value) {
		factory.setQContextSupplier(value);
		return this;
	}

	public DummyBeanFactory pluginIndex(PluginIndex instance) {
		factory.index = instance;
		return this;
	}

	public Supplier<Engine> getEngineSupplier() {
		return () -> engine;
	}

	public EngineService getEngineService() {
		return engine;
	}

	public EngineSupplierFactory getEngineSupplierFactory() {
		return factory;
	}

	public void show() {
		UiService main = (UiService) ui.get();
		main.showFrame();
	}
}
