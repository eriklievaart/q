package com.eriklievaart.q.bind.binding;

import javax.swing.KeyStroke;

import com.eriklievaart.toolkit.lang.api.ToString;

public class Binding {

	public final long bundleId;
	public final ActionType event;
	public final Object bound;
	public final ActionWrapper action;
	public final ComponentWrapper component;
	public KeyStroke keyPressed;
	public KeyStroke keyReleased;

	public Binding(BindingInfo info, ComponentWrapper component, ActionWrapper action) {
		this.bundleId = info.bundleId;
		this.event = info.event;
		this.component = component;
		this.action = action;
		this.bound = event.bind(this);

		initKeyStrokes(info);
	}

	private void initKeyStrokes(BindingInfo info) {
		this.keyPressed = info.keyPressed;
		this.keyReleased = info.keyReleased;
	}

	@Override
	public String toString() {
		String componentId = component == null ? "?" : component.instance.getName();

		if (event == ActionType.KEY_PRESSED) {
			return keyToString(componentId, keyPressed);
		}
		if (event == ActionType.KEY_RELEASED) {
			return keyToString(componentId, keyReleased);
		}
		return ToString.simple(this, "$[$->$]", componentId, event);
	}

	private String keyToString(String componentId, KeyStroke key) {
		String stroke = key == null ? "?" : key.toString();
		return ToString.simple(this, "$[$->$]", componentId, stroke);
	}
}