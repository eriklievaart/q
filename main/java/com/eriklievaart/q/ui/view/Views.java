package com.eriklievaart.q.ui.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.ui.main.UiComponents;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class Views {
	private LogTemplate log = new LogTemplate(getClass());

	private UiComponents components;
	private Map<String, QView> views = new LinkedHashMap<>();
	private boolean tabsVisible = false;

	public Views(UiComponents components) {
		this.components = components;
	}

	public void show(QView view) {
		String id = view.getId();
		log.trace("showing view %", id);

		boolean exists = views.containsKey(id);
		views.put(id, view);

		if (tabsVisible && !exists) {
			components.mainTabs.addTab(view.getLabel(), view.getComponent());
		}
		if (!tabsVisible && views.size() > 1) {
			showTabs();
		}
		if (tabsVisible) {
			List<String> tabOrder = new ArrayList<>(views.keySet());
			components.mainTabs.setSelectedIndex(tabOrder.indexOf(id));
		}
		components.mainFrame.validate();
	}

	public void hide(String id) {
		log.trace("hiding view %", id);

		views.remove(id);
		if (views.size() < 2) {
			hideTabs();
		}
	}

	private void showTabs() {
		components.mainTabs.removeAll();
		views.values().forEach(view -> {
			components.mainTabs.addTab(view.getLabel(), view.getComponent());
		});
		components.mainFrame.getContentPane().remove(components.mainPanel);
		components.mainFrame.getContentPane().add(components.mainTabs, BorderLayout.CENTER);
		tabsVisible = true;
	}

	public void hideTabs() {
		components.mainTabs.removeAll();
		components.mainFrame.getContentPane().remove(components.mainTabs);
		components.mainFrame.getContentPane().add(components.mainPanel, BorderLayout.CENTER);
		tabsVisible = false;
	}

	public void openBrowserTab() {
		if (tabsVisible) {
			components.mainTabs.setSelectedIndex(0);
		}
	}
}