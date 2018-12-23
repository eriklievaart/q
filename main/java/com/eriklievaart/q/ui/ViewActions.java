package com.eriklievaart.q.ui;

import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.ui.main.UiComponents;
import com.eriklievaart.q.ui.render.label.LabelStyler;

public class ViewActions {

	private UiBeanFactory beans;

	public ViewActions(UiBeanFactory beans) {
		this.beans = beans;
	}

	public void putActions(Map<String, Consumer<ActionContext>> map) {
		map.put("q.view.left", c -> focusLeft());
		map.put("q.view.right", c -> focusRight());
		map.put("q.view.hide", c -> hideAll());
		map.put("q.view.hidden", c -> beans.getContextMediator().toggleHidden());
		map.put("q.view.swap", c -> beans.getContextMediator().swap());
		map.put("q.view.mirror", c -> beans.getContextMediator().mirror());
	}

	private void focusLeft() {
		beans.getViews().openBrowserTab();
		beans.getComponents().leftBrowser.fileList.requestFocus();
		LabelStyler.styleNormal(beans.getComponents().leftBrowser.urlLabel);
		LabelStyler.styleSubtle(beans.getComponents().rightBrowser.urlLabel);
	}

	private void focusRight() {
		beans.getViews().openBrowserTab();
		beans.getComponents().rightBrowser.fileList.requestFocus();
		LabelStyler.styleSubtle(beans.getComponents().leftBrowser.urlLabel);
		LabelStyler.styleNormal(beans.getComponents().rightBrowser.urlLabel);
	}

	private void hideAll() {
		UiComponents components = beans.getComponents();
		beans.getViews().hideTabs();
		components.southPanel.remove(components.commandField);
		components.mainFrame.validate();
	}
}
