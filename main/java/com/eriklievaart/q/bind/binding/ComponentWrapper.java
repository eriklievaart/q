package com.eriklievaart.q.bind.binding;

import java.awt.Component;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class ComponentWrapper {

	public final long bundleId;
	public final Component instance;

	public ComponentWrapper(long bundleId, Component component) {
		Check.notNull(component);
		this.bundleId = bundleId;
		this.instance = component;
	}
}
