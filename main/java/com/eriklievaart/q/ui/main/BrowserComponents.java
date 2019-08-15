package com.eriklievaart.q.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.event.BrowserObserver;
import com.eriklievaart.q.ui.render.Theme;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapperColorFactory;
import com.eriklievaart.q.ui.render.list.QListCellRenderer;

public class BrowserComponents {

	public JPanel panel = new JPanel(new BorderLayout());
	public JLabel urlLabel = new JLabel();
	public DefaultListModel<VirtualFileWrapper> fileListModel = new DefaultListModel<>();
	public JList<VirtualFileWrapper> fileList = new JList<>(fileListModel);
	public BrowserObserver observer = new BrowserObserver(this);

	public final BrowserOrientation orientation;

	public BrowserComponents(BrowserOrientation orientation) {
		this.orientation = orientation;
	}

	public void init(UiBeanFactory beans) {
		urlLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		QListCellRenderer<VirtualFileWrapper> renderer = new QListCellRenderer<>();
		renderer.setForegroundFactory(new VirtualFileWrapperColorFactory());
		renderer.setIconFactory(beans.getIconFactory());
		fileList.setCellRenderer(renderer);
		fileList.getInputMap().put(KeyStroke.getKeyStroke("control C"), null);

		panel.add(new JScrollPane(fileList), BorderLayout.CENTER);
		panel.add(urlLabel, BorderLayout.NORTH);
	}

	public void store(Map<String, Component> map) {
		String prefix = "q." + orientation.name().toLowerCase();
		map.put(prefix + ".panel", panel);
		map.put(prefix + ".url.label", urlLabel);
		map.put(prefix + ".browser.list", fileList);
	}

	public void applyTheme() {
		fileList.setBackground(Theme.BACKGROUND_COLOR);
		panel.setBackground(Theme.BACKGROUND_COLOR);
	}
}