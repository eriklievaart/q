package com.eriklievaart.q.bind.menu;

import javax.swing.JMenuItem;

import com.eriklievaart.toolkit.lang.api.ToString;

public class JMenuItemWrapper {

	public int order = 0;
	public JMenuItem item = new JMenuItem();
	public String action;

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", action);
	}
}
