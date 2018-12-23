package com.eriklievaart.q.ui.api;

import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.ListCellRenderer;

import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.api.render.ColorFactory;

public interface QMainUi {

	public QContext getQContext();

	public void showView(QView qView);

	public void showBrowser();

	public void hideView(String id);

	public <E> ListCellRenderer<E> createListCellRenderer(ColorFactory foreground);

	public void swapBrowsers();

	public void navigateFuzzy(String orientation, String path);

	public void setMenuBar(JMenuBar menu);

	public List<String> getRecentlyVisitedDirectories();
}
