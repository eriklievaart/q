package com.eriklievaart.q.bind.registry;

import javax.swing.JPanel;

import org.junit.Test;

import com.eriklievaart.q.bind.parse.UiConfig;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;

public class BindingRegistryU {

	@Test
	public void addValid() {
		BindingRegistry testable = new BindingRegistry();

		JPanel panel = new JPanel();
		StubUi ui = ComponentNodeBuilder.create("panel.id", binding -> binding.action("ignore"));
		ui.putComponent("panel.id", panel);
		ui.putAction("action.id", System.out::println);

		testable.add(MapTool.of(2l, ui));
		CheckCollection.isSize(testable.components, 1);
		CheckCollection.isSize(testable.actions, 1);
	}

	@Test
	public void addInvalidIniNodes() {
		BindingRegistry testable = new BindingRegistry();

		String invalidId = null;
		JPanel panel = new JPanel();
		StubUi ui = ComponentNodeBuilder.create(invalidId, binding -> binding.action("ignore"));
		ui.putComponent("panel.id", panel);
		ui.putAction("ignore", System.out::println);

		UiConfig config = testable.add(MapTool.of(2l, ui));
		CheckCollection.isEmpty(testable.components);
		CheckCollection.isEmpty(testable.actions);
		CheckCollection.isEmpty(config.getBindings());
	}

	@Test
	public void addInvalidComponentId() {
		BindingRegistry testable = new BindingRegistry();

		JPanel panel = new JPanel();
		StubUi ui = ComponentNodeBuilder.create("panel.id", binding -> binding.action("ignore"));
		ui.putComponent("panel/id", panel);

		testable.add(MapTool.of(2l, ui));
		CheckCollection.isEmpty(testable.components);
		CheckCollection.isEmpty(testable.actions);
	}

	@Test
	public void addInvalidActionId() {
		BindingRegistry testable = new BindingRegistry();

		StubUi ui = ComponentNodeBuilder.create("panel.id", binding -> binding.action("ignore"));
		ui.putAction("id/invalid", System.out::println);

		testable.add(MapTool.of(2l, ui));
		CheckCollection.isEmpty(testable.components);
		CheckCollection.isEmpty(testable.actions);
	}
}
