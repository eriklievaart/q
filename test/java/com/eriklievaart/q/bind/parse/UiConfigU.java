package com.eriklievaart.q.bind.parse;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.junit.Test;

import com.eriklievaart.q.bind.binding.ActionWrapper;
import com.eriklievaart.q.bind.menu.JMenuItemWrapper;
import com.eriklievaart.q.bind.menu.JMenuWrapper;
import com.eriklievaart.q.bind.registry.BindingRegistry;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class UiConfigU {

	@Test
	public void createMenuBar() throws Exception {
		UiConfig config = new UiConfig();

		config.menu.get("second").menu.setText("second");
		config.menu.get("second").order = 2;
		config.menu.get("third").menu.setText("third");
		config.menu.get("third").order = 3;
		config.menu.get("first").menu.setText("first");
		config.menu.get("first").order = 1;

		JMenuBar bar = config.createMenuBar(new BindingRegistry());
		Component[] components = bar.getComponents();
		Check.isEqual(getMenuLabel(components[0]), "first");
		Check.isEqual(getMenuLabel(components[1]), "second");
		Check.isEqual(getMenuLabel(components[2]), "third");
	}

	@Test
	public void createMenuWithItems() throws Exception {
		UiConfig config = new UiConfig();
		BindingRegistry registry = new BindingRegistry();
		registry.putAction("noop", new ActionWrapper(-1l, "noop", p -> p.toString()));

		JMenuWrapper parent = config.menu.get("menu");
		parent.menu.setText("Menu");

		JMenuItemWrapper second = new JMenuItemWrapper();
		second.order = 2;
		second.action = "noop";
		second.item.setText("second");
		parent.addItem(second);

		JMenuItemWrapper third = new JMenuItemWrapper();
		third.order = 3;
		third.action = "noop";
		third.item.setText("third");
		parent.addItem(third);

		JMenuItemWrapper first = new JMenuItemWrapper();
		first.order = 1;
		first.action = "noop";
		first.item.setText("first");
		parent.addItem(first);

		JMenuBar bar = config.createMenuBar(registry);
		JMenu menu = (JMenu) bar.getComponents()[0];
		Check.isEqual(getMenuItemLabel(menu.getMenuComponent(0)), "first");
		Check.isEqual(getMenuItemLabel(menu.getMenuComponent(1)), "second");
		Check.isEqual(getMenuItemLabel(menu.getMenuComponent(2)), "third");
	}

	private String getMenuItemLabel(Component component) {
		JMenuItem menu = (JMenuItem) component;
		return menu.getText();
	}

	private String getMenuLabel(Component component) {
		JMenu menu = (JMenu) component;
		return menu.getText();
	}

}
