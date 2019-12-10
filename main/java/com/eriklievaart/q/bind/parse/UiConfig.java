package com.eriklievaart.q.bind.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;

import com.eriklievaart.q.bind.binding.BindingInfo;
import com.eriklievaart.q.bind.menu.JMenuItemWrapper;
import com.eriklievaart.q.bind.menu.JMenuWrapper;
import com.eriklievaart.q.bind.registry.BindingRegistry;
import com.eriklievaart.toolkit.lang.api.collection.LazyMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class UiConfig {

	private List<BindingInfo> bindings = NewCollection.list();
	Map<String, JMenuWrapper> menu = new LazyMap<>(k -> new JMenuWrapper(k));

	public List<BindingInfo> getBindings() {
		return bindings;
	}

	public void addBindings(List<BindingInfo> entries) {
		bindings.addAll(entries);
	}

	public JMenuBar createMenuBar(BindingRegistry registry) {
		JMenuBar bar = new JMenuBar();

		List<JMenuWrapper> wrappers = new ArrayList<>(menu.values());
		Collections.sort(wrappers, (a, b) -> a.order - b.order);
		for (JMenuWrapper wrapper : wrappers) {
			bar.add(wrapper.menu);
			addChildren(wrapper, registry);
		}
		return bar;
	}

	private void addChildren(JMenuWrapper wrapper, BindingRegistry registry) {
		List<JMenuItemWrapper> wrappers = new ArrayList<>(wrapper.getItems());
		Collections.sort(wrappers, (a, b) -> a.order - b.order);

		for (JMenuItemWrapper child : wrappers) {
			wrapper.menu.add(child.item);
			child.item.addActionListener(registry.createActionListener(child.action));
		}
	}
}