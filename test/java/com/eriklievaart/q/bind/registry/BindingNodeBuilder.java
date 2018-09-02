package com.eriklievaart.q.bind.registry;

import com.eriklievaart.toolkit.io.api.ini.IniNode;

public class BindingNodeBuilder {

	final IniNode node;

	public BindingNodeBuilder() {
		node = new IniNode("binding");
	}

	public BindingNodeBuilder event(String event) {
		node.setProperty("event", event);
		return this;
	}

	public BindingNodeBuilder action(String action) {
		node.setProperty("action", action);
		return this;
	}

	public BindingNodeBuilder keyReleased(String... key) {
		node.setProperty("keyReleased", String.join(", ", key));
		return this;
	}
}
