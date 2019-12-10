package com.eriklievaart.q.bind.parse;

import java.util.List;

import javax.swing.KeyStroke;

import com.eriklievaart.q.bind.menu.JMenuItemWrapper;
import com.eriklievaart.q.bind.menu.JMenuWrapper;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class MenuParser {
	private static final String ACTION_PROPERTY = "action";
	private static final String MENU_NODE = "menu";
	private static final String LABEL_PROPERTY = "label";
	private static final String MNEMONIC_PROPERTY = "mnemonic";
	private static final String ACCELERATOR_PROPERTY = "accelerator";

	private static final String ORDER_PROPERTY = "order";
	private static final String MAX_ORDER = "" + Integer.MAX_VALUE;

	private UiConfig config;

	public MenuParser(UiConfig config) {
		this.config = config;
	}

	public void parse(List<IniNode> nodes) {
		for (IniNode node : nodes) {
			if (node.getName().equals(MENU_NODE)) {
				JMenuWrapper wrapper = config.menu.get(node.getIdentifier());
				parseOrder(node, wrapper);
				node.ifProperty(MNEMONIC_PROPERTY, v -> wrapper.menu.setMnemonic(v.charAt(0)));
				node.ifProperty(LABEL_PROPERTY, v -> wrapper.menu.setText(v));

				for (IniNode child : node.getChildren("item")) {
					parseItem(wrapper, child);
				}
			}
		}
	}

	private void parseOrder(IniNode node, JMenuWrapper wrapper) {
		if (wrapper.order == null) {
			wrapper.order = Integer.parseInt(node.getPropertyOrDefault(ORDER_PROPERTY, MAX_ORDER));
		} else {
			node.ifProperty(ORDER_PROPERTY, v -> {
				wrapper.order = Integer.parseInt(node.getPropertyOrDefault(ORDER_PROPERTY, MAX_ORDER));
			});
		}
	}

	private void parseItem(JMenuWrapper parent, IniNode node) {
		JMenuItemWrapper wrapper = new JMenuItemWrapper();
		wrapper.order = Integer.parseInt(node.getPropertyOrDefault(ORDER_PROPERTY, MAX_ORDER));
		wrapper.action = node.getProperty(ACTION_PROPERTY);
		wrapper.item.setText(node.getProperty(LABEL_PROPERTY));
		node.ifProperty(MNEMONIC_PROPERTY, v -> wrapper.item.setMnemonic(v.charAt(0)));
		if (node.hasProperty(ACCELERATOR_PROPERTY)) {
			String property = node.getProperty(ACCELERATOR_PROPERTY);
			KeyStroke key = KeyStroke.getKeyStroke(property);
			Check.notNull(key, "Invalid KeyStroke %", property);
			wrapper.item.setAccelerator(key);
		}
		parent.addItem(wrapper);
	}
}