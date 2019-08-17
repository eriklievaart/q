package com.eriklievaart.q.zexecute;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.ThreadPolicy;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class ExecuteService implements QPlugin, QUi {
	private ExecuteController controller;
	private Supplier<Engine> engine;

	public ExecuteService(Supplier<QMainUi> supplier, Supplier<Engine> engine) {
		this.engine = engine;
		this.controller = new ExecuteController(supplier);
	}

	@Override
	public Invokable createInstance() {
		return new ExecuteShellCommand(controller);
	}

	@Override
	public String getCommandName() {
		return "execute";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.FORK;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.BOTH;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return NewCollection.map();
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> actions = NewCollection.map();
		actions.put("q.execute.each.frame", c -> controller.each(engine, true));
		actions.put("q.execute.each.silent", c -> controller.each(engine, false));
		return actions;
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/zexecute/execute-bind.txt");
	}
}
