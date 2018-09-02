package com.eriklievaart.q.ui;

import java.awt.Component;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuBar;
import javax.swing.ListCellRenderer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.api.render.ColorFactory;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.context.LruIndex;
import com.eriklievaart.q.ui.main.UiController;
import com.eriklievaart.q.ui.render.list.QListCellRenderer;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class UiService implements QUi, QMainUi {

	private final UiController controller;
	private final LruIndex index;

	public UiService(UiBeanFactory beans) {
		controller = beans.getController();
		index = beans.getLruIndex();
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return controller.getComponentMap();
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return controller.getActionMap();
	}

	@Override
	public InputStream getBindings() {
		InputStream is = getClass().getResourceAsStream(UiResourcePaths.BINDINGS);
		Check.notNull(is, "Unable to load bindings");
		return is;
	}

	@Override
	public QContext getQContext() {
		return controller.getContext();
	}

	public void shutdown() {
		controller.shutdown();
	}

	@Override
	public void showView(QView view) {
		SwingThread.invokeLater(() -> {
			controller.showView(view);
		});
	}

	@Override
	public void hideView(String id) {
		SwingThread.invokeLater(() -> {
			controller.hideView(id);
		});
	}

	@Override
	public <E> ListCellRenderer<E> createListCellRenderer(ColorFactory foreground) {
		QListCellRenderer<E> renderer = new QListCellRenderer<>();
		renderer.setForegroundFactory(foreground);
		return renderer;
	}

	@Override
	public void swapBrowsers() {
		controller.swapBrowsers();
	}

	@Override
	public void navigateFuzzy(String orientation, String location) {
		controller.navigateFuzzy(BrowserOrientation.valueOf(orientation.trim().toUpperCase()), location);
	}

	@Override
	public void setMenuBar(JMenuBar menu) {
		controller.setMenuBar(menu);
	}

	public void showFrame() {
		controller.showFrame();
	}

	@Override
	public List<String> getRecentlyVisitedDirectories() {
		return index.getRecentlyVisited();
	}

	@Override
	public void showBrowser() {
		controller.showBrowser();
	}
}