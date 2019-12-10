package com.eriklievaart.q.zlocation;

import java.util.function.Supplier;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.ui.api.QMainUi;

@Doc("open a location in the filebrowser")
class LocationShellCommand implements Invokable {

	private Event event;
	private String path;
	private String orientation;
	private Supplier<QMainUi> ui;

	private enum Event {
		URL, HOME, SWAP;
	}

	public LocationShellCommand(Supplier<QMainUi> supplier) {
		this.ui = supplier;
	}

	@Flag(group = "main", values = { "$url" }, primary = true)
	@Doc("Open an URL. Takes absolute or relative path as argument")
	public LocationShellCommand url(final String url) {
		event = Event.URL;
		path = url;
		return this;
	}

	@Flag(group = "main")
	@Doc("Swap left and right")
	public LocationShellCommand swap() {
		event = Event.SWAP;
		return this;
	}

	@Flag(group = "orientation", primary = true)
	@Doc("Open location in the active file browser window")
	public LocationShellCommand active() {
		orientation = "active";
		return this;
	}

	@Flag(group = "orientation")
	@Doc("Open location in the inactive file browser window")
	public LocationShellCommand inactive() {
		orientation = "inactive";
		return this;
	}

	@Flag(group = "orientation")
	@Doc("Open location in the left file browser window")
	public LocationShellCommand left() {
		orientation = "left";
		return this;
	}

	@Flag(group = "orientation")
	@Doc("Open location in the right file browser window")
	public LocationShellCommand right() {
		orientation = "right";
		return this;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		QMainUi events = ui.get();
		if (events == null) {
			return;
		}
		switch (event) {

		case SWAP:
			events.swapBrowsers();
			return;

		case URL:
			events.navigateFuzzy(orientation, path);
			return;
		}
		throw new IllegalStateException("Unknown Event " + event);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
	}
}