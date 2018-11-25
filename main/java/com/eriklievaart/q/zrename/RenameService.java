package com.eriklievaart.q.zrename;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.zrename.ui.RenameController;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class RenameService implements QPlugin, QUi {

	private RenameController controller;

	public RenameService(Supplier<QMainUi> ui, Supplier<Engine> engine) {
		this.controller = new RenameController(ui, engine);
	}

	@Override
	public Map<String, Component> getComponentMap() {
		Map<String, Component> map = NewCollection.map();
		addComponents(map);
		return map;
	}

	private void addComponents(Map<String, Component> map) {
		map.put("q.rename.main.panel", controller.mainPanel);
		map.put("q.rename.criteria.panel", controller.criteriaPanel);
		map.put("q.rename.list.panel", controller.listPanel);
		map.put("q.rename.regex.label", controller.regexLabel);
		map.put("q.rename.regex.field", controller.regexField);
		map.put("q.rename.rename.label", controller.renameLabel);
		map.put("q.rename.rename.field", controller.renameField);
		map.put("q.rename.from.list", controller.fromList);
		map.put("q.rename.to.list", controller.toList);
		map.put("q.rename.accept.button", controller.acceptButton);
		map.put("q.rename.refresh.button", controller.refreshButton);
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> map = NewCollection.map();

		map.put("q.rename.ui", c -> controller.showUi());
		map.put("q.rename.regex", c -> controller.regexUpdated());
		map.put("q.rename.do", c -> controller.doRename());
		map.put("q.rename.refresh", c -> controller.updateListFiles());

		return map;
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/zrename/rename-bind.txt");
	}

	@Override
	public String getCommandName() {
		return "rename";
	}

	@Override
	public Invokable createInstance() {
		return new RenameShellCommand(controller);
	}

}
