package com.eriklievaart.q.zfind;

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
import com.eriklievaart.q.zfind.ui.FindController;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class FindService implements QPlugin, QUi {

	private FindController controller;

	public FindService(Supplier<QMainUi> ui, Supplier<Engine> engine) {
		this.controller = new FindController(ui, engine);
	}

	@Override
	public Invokable createInstance() {
		return new FindShellCommand(controller);
	}

	@Override
	public String getCommandName() {
		return "find";
	}

	@Override
	public ThreadPolicy getThreadPolicy() {
		return ThreadPolicy.SINGLE;
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.BOTH;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		Map<String, Component> map = NewCollection.map();
		map.put("q.find.panel", controller.panel);
		map.put("q.find.list", controller.list);
		map.put("q.find.copy.button", controller.copyButton);
		map.put("q.find.move.button", controller.moveButton);
		map.put("q.find.delete.button", controller.deleteButton);
		return map;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> map = NewCollection.map();
		map.put("q.find.copy", c -> controller.copy());
		map.put("q.find.move", c -> controller.move());
		map.put("q.find.delete", c -> controller.delete());
		map.put("q.find.parent", c -> controller.parent());
		map.put("q.find.open", c -> controller.open());
		map.put("q.find.do", c -> controller.find());
		map.put("q.find.clean", c -> controller.cleanList());
		return map;
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/zfind/find-bind.txt");
	}
}
