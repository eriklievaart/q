package com.eriklievaart.q.zindex;

import java.util.function.Supplier;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;

public class IndexPlugin implements QPlugin {

	private Supplier<QMainUi> ui;
	private Supplier<UrlResolver> resolver;

	public IndexPlugin(Supplier<QMainUi> supplier, Supplier<UrlResolver> resolver) {
		this.ui = supplier;
		this.resolver = resolver;
	}

	@Override
	public String getCommandName() {
		return "index";
	}

	@Override
	public Invokable createInstance() {
		return new IndexShellCommand(ui, resolver);
	}
}
