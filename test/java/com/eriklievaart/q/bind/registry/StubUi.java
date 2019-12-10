package com.eriklievaart.q.bind.registry;

import java.awt.Component;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;

public class StubUi implements QUi {

	private Map<String, Component> components = new Hashtable<>();
	private Map<String, Consumer<ActionContext>> actions = new Hashtable<>();
	private InputStream is;

	public StubUi(InputStream is) {
		this.is = is;
	}

	public void putComponent(String id, Component component) {
		components.put(id, component);
	}

	public void putAction(String id, Consumer<ActionContext> action) {
		actions.put(id, action);
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return components;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return actions;
	}

	@Override
	public InputStream getBindings() {
		return is;
	}
}