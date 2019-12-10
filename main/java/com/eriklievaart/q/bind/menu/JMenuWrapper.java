package com.eriklievaart.q.bind.menu;

import java.util.List;

import javax.swing.JMenu;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class JMenuWrapper {

	private String id;
	public Integer order = null;
	public JMenu menu = new JMenu();
	private List<JMenuItemWrapper> children = NewCollection.list();

	public JMenuWrapper(String id) {
		this.id = id;
	}

	public void addItem(JMenuItemWrapper item) {
		children.add(item);
	}

	public List<JMenuItemWrapper> getItems() {
		return children;
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", id);
	}
}