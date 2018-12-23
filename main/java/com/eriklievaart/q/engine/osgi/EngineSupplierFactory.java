package com.eriklievaart.q.engine.osgi;

import java.util.function.Supplier;

import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.convert.BasicConvertersProvider;
import com.eriklievaart.q.engine.convert.CollectionConvertersProvider;
import com.eriklievaart.q.engine.impl.EngineTemplates;
import com.eriklievaart.q.engine.impl.InputExaminer;
import com.eriklievaart.q.engine.impl.PluginContextImpl;
import com.eriklievaart.q.engine.impl.PluginRunner;
import com.eriklievaart.q.engine.impl.VariableResolver;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.q.engine.ui.EngineUi;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.Converters;

public class EngineSupplierFactory {

	private Supplier<UrlResolver> urlResolverSupplier;
	private Supplier<QContext> contextSupplier;
	private Supplier<QMainUi> mainUiSupplier;

	PluginIndex index = new PluginIndex();
	private final VariableResolver resolver = new VariableResolver();
	private final EngineTemplates templates = new EngineTemplates();
	private final EngineUi ui = new EngineUi(this);

	public EngineSupplierFactory(Supplier<UrlResolver> url, Supplier<QContext> context, Supplier<QMainUi> mainUi) {
		this.urlResolverSupplier = url;
		this.contextSupplier = context;
		this.mainUiSupplier = mainUi;
	}

	public PluginIndex getPluginIndex() {
		return index;
	}

	public PluginRunner getPluginRunner() {
		return new PluginRunner(index, getCollectionsConvertersSupplier());
	}

	public Supplier<Converters> getCollectionsConvertersSupplier() {
		return new CollectionConvertersProvider(urlResolverSupplier);
	}

	public Supplier<Converters> getBasicConvertersSupplier() {
		return new BasicConvertersProvider(urlResolverSupplier);
	}

	public Supplier<QContext> getQContextSupplier() {
		return contextSupplier;
	}

	public void setQContextSupplier(Supplier<QContext> supplier) {
		contextSupplier = supplier;
	}

	public Supplier<UrlResolver> getUrlResolverSupplier() {
		return urlResolverSupplier;
	}

	public Supplier<QMainUi> getMainUiSupplier() {
		return mainUiSupplier;
	}

	public EngineTemplates getTemplates() {
		return templates;
	}

	public VariableResolver getResolver() {
		return resolver;
	}

	public EngineUi getEngineUi() {
		return ui;
	}

	public PluginContextImpl getPluginContext(ShellCommand command) {
		return new PluginContextImpl(command, contextSupplier.get(), resolver);
	}

	public InputExaminer getInputExaminer() {
		return new InputExaminer(this);
	}
}
