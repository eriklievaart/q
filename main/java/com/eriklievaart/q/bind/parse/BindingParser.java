package com.eriklievaart.q.bind.parse;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.KeyStroke;

import com.eriklievaart.q.bind.binding.ActionType;
import com.eriklievaart.q.bind.binding.BindingInfo;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

class BindingParser {
	private static final String ACTION_PROPERTY = "action";
	private static final String COMPONENT_NODE_NAME = "component";
	private static final String KEY_PRESSED_PROPERTY = "keyPressed";
	private static final String KEY_RELEASED_PROPERTY = "keyReleased";

	private Long bundleId;

	public BindingParser(long bundle) {
		this.bundleId = bundle;
	}

	List<BindingInfo> parse(List<IniNode> nodes) {
		List<BindingInfo> result = NewCollection.list();
		for (IniNode componentNode : nodes) {
			if (!componentNode.getName().equalsIgnoreCase(COMPONENT_NODE_NAME)) {
				continue;
			}
			String id = componentNode.getIdentifier();
			Check.notBlank(id, "$) missing identifier for component", componentNode.getLineNumber());

			for (IniNode bindingNode : componentNode.getChildren()) {
				result.addAll(parse(id, bindingNode));
			}
		}
		return result;
	}

	private Collection<? extends BindingInfo> parse(String componentId, IniNode bindingNode) {
		List<BindingInfo> result = NewCollection.list();

		keyStrokeProperty(bindingNode, KEY_RELEASED_PROPERTY, key -> result.add(parseKeyReleased(key)));
		keyStrokeProperty(bindingNode, KEY_PRESSED_PROPERTY, key -> result.add(parseKeyPressed(key)));
		if (bindingNode.hasProperty("event")) {
			result.add(parseEvent(bindingNode.getProperty("event")));
		}
		for (BindingInfo binding : result) {
			boolean hasAction = bindingNode.hasProperty(ACTION_PROPERTY);
			Check.isTrue(hasAction, "$) No action defined for binding", bindingNode.getLineNumber());
			String actionId = bindingNode.getProperty(ACTION_PROPERTY);
			binding.actionId = actionId;
		}
		result.forEach(b -> b.componentId = componentId);
		return result;
	}

	public void keyStrokeProperty(IniNode node, String property, Consumer<String> consumer) {
		if (node.hasProperty(property)) {
			for (String key : node.getProperty(property).split("\\s*+,\\s*+")) {
				consumer.accept(key);
			}
		}
	}

	private BindingInfo parseEvent(String property) {
		BindingInfo binding = new BindingInfo();
		binding.bundleId = bundleId;
		binding.event = ActionType.valueOf(property.toUpperCase());
		return binding;
	}

	private BindingInfo parseKeyPressed(String key) {
		BindingInfo binding = new BindingInfo();
		binding.keyPressed = parseKeyStroke(key);
		binding.bundleId = bundleId;
		binding.event = ActionType.KEY_PRESSED;
		return binding;
	}

	private BindingInfo parseKeyReleased(String key) {
		BindingInfo binding = new BindingInfo();
		binding.keyReleased = parseKeyStroke(key);
		binding.bundleId = bundleId;
		binding.event = ActionType.KEY_RELEASED;
		return binding;
	}

	private KeyStroke parseKeyStroke(String key) {
		KeyStroke stroke = KeyStroke.getKeyStroke(key.trim());
		Check.notNull(stroke, "Could not parse KeyStroke %", key);
		return stroke;
	}
}