package com.eriklievaart.q.bind.parse;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JList;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.bind.binding.ActionType;
import com.eriklievaart.q.bind.binding.BindingInfo;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.mock.BombSquad;

public class BindingParserU {

	private Map<String, Component> components;
	private Map<String, Consumer<ActionContext>> actions;

	@Before
	public void init() {
		components = new Hashtable<>();
		actions = new Hashtable<>();
	}

	@Test
	public void parseKeyBindingWithoutComponentId() {
		IniNode componentNode = new IniNode("component");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		BombSquad.diffuse(AssertionException.class, "missing identifier", () -> {
			new BindingParser(0).parse(Arrays.asList(componentNode));
		});
	}

	@Test
	public void parseKeyBindingWithoutAction() {
		components.put("q.left.browser", new JList<String>());

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("keyReleased", "alt UP");

		BombSquad.diffuse(AssertionException.class, "action defined", () -> {
			new BindingParser(0).parse(Arrays.asList(componentNode));
		});
	}

	@Test
	public void parseUiBinding() {
		components.put("q.left.browser", new JList<String>());
		actions.put("q.foo", (ctx) -> {
		});

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("event", "click3");
		bindingNode.setProperty("action", "q.foo");

		List<BindingInfo> result = new BindingParser(1).parse(Arrays.asList(componentNode));
		Assertions.assertThat(result).hasSize(1);
		BindingInfo binding = result.get(0);

		Assertions.assertThat(binding.event).isEqualTo(ActionType.CLICK3);

		Assertions.assertThat(binding.bundleId).isEqualTo(1);
		Assertions.assertThat(binding.componentId).isEqualTo("q.left.browser");
		Assertions.assertThat(binding.actionId).isEqualTo("q.foo");
		Assertions.assertThat(binding.keyReleased).isNull();
	}

	@Test
	public void parseKeyPressed() {
		components.put("q.left.browser", new JList<String>());
		actions.put("q.foo", (ctx) -> {
		});

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("keyPressed", "alt UP");
		bindingNode.setProperty("action", "q.foo");

		List<BindingInfo> result = new BindingParser(2).parse(Arrays.asList(componentNode));
		Assertions.assertThat(result).hasSize(1);
		BindingInfo binding = result.get(0);

		Assertions.assertThat(binding.event).isEqualTo(ActionType.KEY_PRESSED);
		Assertions.assertThat(binding.keyPressed.getKeyCode()).isEqualTo(KeyEvent.VK_UP);
		Assertions.assertThat(binding.keyPressed.getModifiers()).isEqualTo(KeyEvent.ALT_MASK + KeyEvent.ALT_DOWN_MASK);

		Assertions.assertThat(binding.bundleId).isEqualTo(2);
		Assertions.assertThat(binding.componentId).isEqualTo("q.left.browser");
		Assertions.assertThat(binding.actionId).isEqualTo("q.foo");
	}

	@Test
	public void parseKeyReleased() {
		components.put("q.left.browser", new JList<String>());
		actions.put("q.foo", (ctx) -> {
		});

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("keyReleased", "alt UP");
		bindingNode.setProperty("action", "q.foo");

		List<BindingInfo> result = new BindingParser(2).parse(Arrays.asList(componentNode));
		Assertions.assertThat(result).hasSize(1);
		BindingInfo binding = result.get(0);

		Assertions.assertThat(binding.event).isEqualTo(ActionType.KEY_RELEASED);
		Assertions.assertThat(binding.keyReleased.getKeyCode()).isEqualTo(KeyEvent.VK_UP);
		Assertions.assertThat(binding.keyReleased.getModifiers()).isEqualTo(KeyEvent.ALT_MASK + KeyEvent.ALT_DOWN_MASK);

		Assertions.assertThat(binding.bundleId).isEqualTo(2);
		Assertions.assertThat(binding.componentId).isEqualTo("q.left.browser");
		Assertions.assertThat(binding.actionId).isEqualTo("q.foo");
	}

	@Test
	public void parseKeyBindingMultiple() {
		components.put("q.left.browser", new JList<String>());
		actions.put("q.foo", (ctx) -> {
		});

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("keyReleased", "alt UP, BACK_SPACE");
		bindingNode.setProperty("action", "q.foo");

		List<BindingInfo> result = new BindingParser(0).parse(Arrays.asList(componentNode));
		Assertions.assertThat(result).hasSize(2);
		BindingInfo altUp = result.get(0);
		BindingInfo backspace = result.get(1);

		Assertions.assertThat(altUp.event).isEqualTo(ActionType.KEY_RELEASED);
		Assertions.assertThat(altUp.keyReleased.getKeyCode()).isEqualTo(KeyEvent.VK_UP);
		Assertions.assertThat(altUp.keyReleased.getModifiers()).isEqualTo(KeyEvent.ALT_MASK + KeyEvent.ALT_DOWN_MASK);

		Assertions.assertThat(backspace.event).isEqualTo(ActionType.KEY_RELEASED);
		Assertions.assertThat(backspace.keyReleased.getKeyCode()).isEqualTo(KeyEvent.VK_BACK_SPACE);
		Assertions.assertThat(backspace.keyReleased.getModifiers()).isEqualTo(0);
	}

	@Test
	public void parseKeyAndUiBinding() {
		components.put("q.left.browser", new JList<String>());
		actions.put("q.foo", (ctx) -> {
		});

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("action", "q.foo");
		bindingNode.setProperty("event", "click3");
		bindingNode.setProperty("keyReleased", "alt UP");

		List<BindingInfo> result = new BindingParser(0).parse(Arrays.asList(componentNode));
		Assertions.assertThat(result).hasSize(2);
		BindingInfo key = result.get(0);
		BindingInfo event = result.get(1);

		Assertions.assertThat(key.event).isEqualTo(ActionType.KEY_RELEASED);
		Assertions.assertThat(key.keyReleased.getKeyCode()).isEqualTo(KeyEvent.VK_UP);
		Assertions.assertThat(key.keyReleased.getModifiers()).isEqualTo(KeyEvent.ALT_MASK + KeyEvent.ALT_DOWN_MASK);

		Assertions.assertThat(event.event).isEqualTo(ActionType.CLICK3);
		Assertions.assertThat(event.keyReleased).isNull();
	}

	@Test
	public void parseKeyBindingInvalidKeyStroke() {
		components.put("q.left.browser", new JList<String>());

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("keyReleased", "hit ERROR key");
		bindingNode.setProperty("action", "q.foo");

		BombSquad.diffuse(AssertionException.class, "KeyStroke", () -> {
			new BindingParser(0).parse(Arrays.asList(componentNode));
		});
	}
}
