package com.eriklievaart.q.ui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.eriklievaart.q.api.render.JLabelThemed;
import com.eriklievaart.q.api.render.JListThemed;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.event.BrowserObserver;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapperColorFactory;

public class BrowserComponents {

	public JPanel panel = new JPanel(new BorderLayout());
	public JLabelThemed urlLabel = new JLabelThemed();
	public DefaultListModel<VirtualFileWrapper> fileListModel = new DefaultListModel<>();
	public JListThemed<VirtualFileWrapper> fileList = new JListThemed<>(fileListModel);
	public BrowserObserver observer = new BrowserObserver(this);

	public final BrowserOrientation orientation;

	public BrowserComponents(BrowserOrientation orientation) {
		this.orientation = orientation;
		removeDefaultKeyListener();
	}

	private void removeDefaultKeyListener() {
		for (KeyListener listener : fileList.getKeyListeners()) {
			if (listener.getClass().getName().contains("javax.swing.plaf.basic.BasicListUI")) {
				fileList.removeKeyListener(listener);
			}
		}
	}

	public void init(UiBeanFactory beans) {
		urlLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		fileList.setForegroundFactory(new VirtualFileWrapperColorFactory(fileList));
		fileList.setIconFactory(beans.getIconFactory());
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
}