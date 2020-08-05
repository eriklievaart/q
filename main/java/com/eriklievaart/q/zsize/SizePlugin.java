package com.eriklievaart.q.zsize;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class SizePlugin implements QPlugin, QUi {

	private final SizeController controller;
	private Supplier<Engine> engineSupplier;

	public SizePlugin(Supplier<QMainUi> uiSupplier, Supplier<Engine> engineSupplier) {
		this.engineSupplier = engineSupplier;
		this.controller = new SizeController(uiSupplier);
	}

	@Override
	public SizeShellCommand createInstance() {
		return new SizeShellCommand(controller);
	}

	@Override
	public String getCommandName() {
		return "size";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.SINGLE;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.FLAGS_ONLY;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		Map<String, Component> components = NewCollection.map();
		components.put("q.size.panel", controller.panel);
		components.put("q.size.list", controller.list);
		components.put("q.size.label", controller.summary);
		return components;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return MapTool.of("q.size.calculate", ctx -> {
			engineSupplier.get().invoke("size");
		});
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/zsize/size-bind.txt");
	}
}