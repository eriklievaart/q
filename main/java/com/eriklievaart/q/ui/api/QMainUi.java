package com.eriklievaart.q.ui.api;

import java.util.List;

import javax.swing.JMenuBar;

import com.eriklievaart.q.api.QView;

public interface QMainUi {

	public QContext getQContext();

	public void showView(QView qView);

	public void showBrowser();

	public void hideView(String id);

	public void swapBrowsers();

	public void navigateFuzzy(String orientation, String path);

	public void setMenuBar(JMenuBar menu);

	public List<String> getRecentlyVisitedDirectories();

	public Dialogs getDialogs();
}