package com.eriklievaart.q.zexecute;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.ui.api.Dialogs;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class DummyQMainUi implements QMainUi {
	private LogTemplate log = new LogTemplate(getClass());

	private QContext context = null;
	private List<String> recent = NewCollection.list();
	private Map<String, String> navigation = NewCollection.map();

	public void addRecentlyVisitedDirectory(String path) {
		recent.add(path);
	}

	@Override
	public QContext getQContext() {
		return context;
	}

	public void setQContext(QContext ctx) {
		this.context = ctx;
	}

	@Override
	public void showView(QView view) {
	}

	@Override
	public void hideView(String id) {
	}

	@Override
	public void swapBrowsers() {
	}

	@Override
	public void navigateFuzzy(String orientation, String path) {
		navigation.put(orientation, path);
		log.debug("navigating $ to %", orientation, path);
	}

	public String getActivePath() {
		return navigation.get("active");
	}

	@Override
	public void setMenuBar(JMenuBar menu) {
	}

	@Override
	public List<String> getRecentlyVisitedDirectories() {
		return Collections.unmodifiableList(recent);
	}

	@Override
	public void showBrowser() {
	}

	@Override
	public Dialogs getDialogs() {
		return null;
	}
}