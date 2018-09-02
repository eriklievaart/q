package com.eriklievaart.q.ui.event;

import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserRefresh {
	private LogTemplate log = new LogTemplate(getClass());

	private long opCounter = 0;
	private long refreshOn = Long.MAX_VALUE;
	private VirtualFile location = null;
	private VirtualFile previous = null;
	private boolean shutdown = false;

	public synchronized boolean isShutdown() {
		return shutdown;
	}

	public synchronized void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public synchronized void setLocation(VirtualFile dir) {
		previous = location;
		location = dir;
		refreshOn = 0;
		opCounter++;
	}

	public synchronized boolean isRefreshRequired() {
		return System.currentTimeMillis() >= refreshOn;
	}

	public synchronized Long getRefreshId() {
		return opCounter;
	}

	public synchronized VirtualFile getRefreshLocation() {
		return location;
	}

	public synchronized VirtualFile getPreviousLocation() {
		return previous;
	}

	public synchronized void refreshCompleted(long spent) {
		long sleep = 1000 + 10 * spent;
		if (sleep > 1500) {
			log.trace("Refresh completed, sleep $", sleep);
		}
		refreshOn = System.currentTimeMillis() + sleep;
	}

}
