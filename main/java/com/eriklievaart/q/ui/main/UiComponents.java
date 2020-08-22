package com.eriklievaart.q.ui.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.eriklievaart.q.api.render.JLabelThemed;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.render.icons.Favicon;

public class UiComponents {
	private static final String FRAME_ID = "q.main.frame";

	public JFrame mainFrame = new JFrame();
	public JTabbedPane mainTabs = new JTabbedPane();

	public JPanel northPanel = new JPanel(new GridLayout(0, 1));
	public JPanel mainPanel = new JPanel(new GridLayout(1, 0, 4, 0));
	public JPanel southPanel = new JPanel(new GridLayout(0, 1));

	public JTextField commandField = new JTextField();
	public JLabelThemed assistLabel = new JLabelThemed();

	public BrowserComponents leftBrowser = new BrowserComponents(BrowserOrientation.LEFT);
	public BrowserComponents rightBrowser = new BrowserComponents(BrowserOrientation.RIGHT);

	void init(UiBeanFactory beans) {
		Image favicon = new Favicon().getFavicon();

		northPanel.setVisible(false);
		initMainFrame(favicon);
		initBrowsers(beans);
		southPanel.add(assistLabel);
	}

	public void store(Map<String, Component> map) {
		map.put(FRAME_ID, mainFrame);
		map.put("q.main.north.panel", southPanel);
		map.put("q.main.panel", mainPanel);
		map.put("q.main.south.panel", southPanel);
		map.put("q.main.command.field", commandField);
		map.put("q.main.assist.label", assistLabel);
		map.put("q.main.tabs", mainTabs);

		leftBrowser.store(map);
		rightBrowser.store(map);
	}

	private void initBrowsers(UiBeanFactory beans) {
		leftBrowser.init(beans);
		rightBrowser.init(beans);
		rightBrowser.urlLabel.setSubtleState();
		mainPanel.add(leftBrowser.panel);
		mainPanel.add(rightBrowser.panel);
	}

	private void initMainFrame(Image favicon) {
		mainFrame.setName(FRAME_ID);
		mainFrame.setTitle("Q Filebrowser");
		mainFrame.setIconImage(favicon);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.getContentPane().add(northPanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainFrame.getContentPane().add(southPanel, BorderLayout.SOUTH);
		mainFrame.validate();
	}
}