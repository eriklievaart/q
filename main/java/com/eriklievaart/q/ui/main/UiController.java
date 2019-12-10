package com.eriklievaart.q.ui.main;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuBar;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.ui.ShutdownListener;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.context.ContextMediator;
import com.eriklievaart.q.ui.event.EngineEvent;
import com.eriklievaart.q.ui.render.label.LabelStyler;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class UiController {
	private static final String ABOUT = "/ui/about.txt";

	private final UiComponents components;
	private final ContextMediator mediator;
	private final EngineEvent engine;
	private final Dialogs dialogs;
	private final UiBeanFactory beans;

	public UiController(UiBeanFactory beans) {
		this.beans = beans;
		this.mediator = beans.getContextMediator();
		this.engine = beans.getEngineEvent();
		this.dialogs = beans.getDialogs();
		this.components = beans.getComponents();
		iniUi();
	}

	private void iniUi() {
		SwingThread.invokeAndWaitUnchecked(() -> {
			engine.setParseResultConsumer(this::showCommandAssist);
			initMediator();
			beans.getViews().show(createBrowserView());
			components.init(beans);
			components.mainFrame.pack();
		});
	}

	private void showCommandAssist(EngineResult message) {
		SwingThread.invokeAndWaitUnchecked(() -> {
			if (message.isError()) {
				LabelStyler.styleError(components.assistLabel);
			} else {
				LabelStyler.styleSubtle(components.assistLabel);
			}
			components.assistLabel.setText(message.getMessage());
		});
	}

	private void initMediator() {
		VirtualFile home = new SystemFile(new File("/"));
		mediator.setLocation(BrowserOrientation.LEFT, home);
		mediator.setLocation(BrowserOrientation.RIGHT, home);
	}

	private QView createBrowserView() {
		QView view = new QView("q.browser", components.mainPanel);
		view.setLabel("browser");
		return view;
	}

	public QContext getContext() {
		return mediator.getContext();
	}

	public Map<String, Component> getComponentMap() {
		Map<String, Component> map = new Hashtable<>();
		components.store(map);
		return map;
	}

	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> map = NewCollection.map();

		map.put("q.help.about", c -> dialogs.message(StreamTool.toString(getClass().getResourceAsStream(ABOUT))));
		map.put("q.help.doc", c -> openDocumentation());

		beans.getBrowserActions().putActions(map);
		beans.getCommandActions().putActions(map);
		beans.getViewActions().putActions(map);
		beans.getClipboardActions().putActions(map);
		return map;
	}

	private void openDocumentation() {
		try {
			Desktop.getDesktop().open(beans.getResources().getDocumentationStart());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void shutdown() {
		components.mainFrame.dispose();
		mediator.shutdown();
	}

	public void showView(QView view) {
		beans.getViews().show(view);
	}

	public void hideView(String id) {
		beans.getViews().hide(id);
	}

	public void swapBrowsers() {
		mediator.swap();
	}

	public void navigateFuzzy(BrowserOrientation orientation, String location) {
		VirtualFile directory = mediator.getSelectedDirectory(orientation);
		beans.withUrlResolver(resolver -> {
			VirtualFile resolved = resolver.resolveFuzzy(directory, location);
			navigate(orientation, resolved);
		});
	}

	private void navigate(BrowserOrientation orientation, VirtualFile resolved) {
		if (resolved.exists() && resolved.isDirectory()) {
			mediator.setLocation(orientation, resolved);
		}
	}

	public void setMenuBar(JMenuBar menu) {
		components.mainFrame.setJMenuBar(menu);
	}

	public void showFrame() {
		components.mainFrame.setVisible(true);
	}

	public void showBrowser() {
		components.mainTabs.setSelectedIndex(0);
	}

	public void registerShutdownListener(ShutdownListener shutdown) {
		components.mainFrame.addWindowListener(shutdown);
	}
}