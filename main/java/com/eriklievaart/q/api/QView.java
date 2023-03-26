package com.eriklievaart.q.api;

import javax.swing.JComponent;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class QView {

	private final String id;
	private final JComponent component;
	private String label;

	public QView(String id, JComponent component) {
		Check.noneNull(id, component);
		this.id = id;
		this.component = component;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label == null ? id : label;
	}

	public String getId() {
		return id;
	}

	public JComponent getComponent() {
		return component;
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", id);
	}
}