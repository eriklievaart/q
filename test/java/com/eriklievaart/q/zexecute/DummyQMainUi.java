package com.eriklievaart.q.zexecute;

import java.util.Collections;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.ListCellRenderer;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.api.render.ColorFactory;
import com.eriklievaart.q.ui.api.Dialogs;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;

public class DummyQMainUi implements QMainUi {

	@Override
	public QContext getQContext() {
		return null;
	}

	@Override
	public void showView(QView view) {
	}

	@Override
	public void hideView(String id) {
	}

	@Override
	public <E> ListCellRenderer<E> createListCellRenderer(ColorFactory colors) {
		return null;
	}

	@Override
	public void swapBrowsers() {
	}

	@Override
	public void navigateFuzzy(String orientation, String path) {
	}

	@Override
	public void setMenuBar(JMenuBar menu) {
	}

	@Override
	public List<String> getRecentlyVisitedDirectories() {
		return Collections.emptyList();
	}

	@Override
	public void showBrowser() {
	}

	@Override
	public Dialogs getDialogs() {
		return null;
	}
}