package com.eriklievaart.q.ui;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.ui.clipboard.ClipboardActions;
import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.q.ui.context.ContextMediator;
import com.eriklievaart.q.ui.context.LruIndex;
import com.eriklievaart.q.ui.event.BrowserObserver;
import com.eriklievaart.q.ui.event.EngineEvent;
import com.eriklievaart.q.ui.main.BrowserActions;
import com.eriklievaart.q.ui.main.CommandActions;
import com.eriklievaart.q.ui.main.Dialogs;
import com.eriklievaart.q.ui.main.UiComponents;
import com.eriklievaart.q.ui.main.UiController;
import com.eriklievaart.q.ui.render.browser.FsvIconFactory;
import com.eriklievaart.q.ui.render.browser.LocalIconFactory;
import com.eriklievaart.q.ui.render.browser.LocalIconLoader;
import com.eriklievaart.q.ui.render.list.IconFactory;
import com.eriklievaart.q.ui.view.Views;
import com.eriklievaart.q.vfs.api.UrlResolver;

public class UiBeanFactory {
	private static final boolean LINUX = System.getProperty("os.name").equalsIgnoreCase("linux");

	private final UiController controller;
	private final LruIndex index;
	private final EngineEvent engineEvent;
	private final UiComponents components = new UiComponents();
	private final Dialogs dialogs = new Dialogs(components);
	private final Views views = new Views(components);
	private final ContextMediator mediator;
	private final UiResourcePaths resources;
	private final AtomicReference<IconFactory> icons = new AtomicReference<>();

	private final Supplier<UrlResolver> resolver;

	public UiBeanFactory(UiResourcePaths resources, Supplier<Engine> engine, Supplier<UrlResolver> resolver) {
		this.resolver = resolver;
		this.resources = resources;
		this.engineEvent = new EngineEvent(engine);
		this.index = new LruIndex(resources.getLruCache());
		this.mediator = createContextMediator();
		this.controller = new UiController(this);
	}

	private ContextMediator createContextMediator() {
		BrowserObserver leftObserver = components.leftBrowser.observer;
		BrowserObserver rightObserver = components.rightBrowser.observer;
		return new ContextMediator(index, leftObserver, rightObserver);
	}

	public UiController getController() {
		return controller;
	}

	public UiResourcePaths getResources() {
		return resources;
	}

	public LruIndex getLruIndex() {
		return index;
	}

	public ContextMediator getContextMediator() {
		return mediator;
	}

	public EngineEvent getEngineEvent() {
		return engineEvent;
	}

	public Dialogs getDialogs() {
		return dialogs;
	}

	public void withUrlResolver(Consumer<UrlResolver> consumer) {
		UrlResolver instance = resolver.get();
		if (instance != null) {
			consumer.accept(instance);
		}
	}

	public UiComponents getComponents() {
		return components;
	}

	public Views getViews() {
		return views;
	}

	public ClipboardActions getClipboardActions() {
		return new ClipboardActions(this);
	}

	public BrowserActions getBrowserActions() {
		return new BrowserActions(this);
	}

	public CommandActions getCommandActions() {
		return new CommandActions(this);
	}

	public ViewActions getViewActions() {
		return new ViewActions(this);
	}

	public IconFactory getIconFactory() {
		if (icons.get() == null) {
			if (LINUX && resources.getIconDirectory().exists()) {
				icons.set(new LocalIconFactory(new LocalIconLoader(resources)));
			} else {
				icons.set(new FsvIconFactory());
			}
		}
		return icons.get();
	}
}
