package com.eriklievaart.q.ui.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserObserver {
	private LogTemplate log = new LogTemplate(getClass());

	private BrowserModel model;
	private BrowserComponents components;
	private BrowserRefresh refresh = new BrowserRefresh();
	private AtomicBoolean showHiddenFiles = new AtomicBoolean(true);
	private Thread refreshThread = new Thread(() -> autoRefresh());

	public BrowserObserver(BrowserComponents components) {
		this.model = new BrowserModel(components);
		this.components = components;
		refreshThread.start();
	}

	public void update(VirtualFile dir) {
		log.trace("update($)", dir);
		if (dir.isFile()) {
			return;
		}
		refresh.setLocation(dir);
		components.urlLabel.setText(dir.getPath());
	}

	public List<VirtualFile> getSelection() {
		Stream<VirtualFileWrapper> selection = components.fileList.getSelectedValuesList().stream();
		return selection.map(w -> w.getVirtualFile()).collect(Collectors.toList());
	}

	private void autoRefresh() {
		try {
			while (!refresh.isShutdown()) {
				if (refresh.isRefreshRequired()) {
					refreshBrowser();
				} else {
					Thread.sleep(100);
				}
			}
		} catch (InterruptedException e) {
			log.warn("refresh thread interrupted");
		}
	}

	private void refreshBrowser() {
		long id = refresh.getRefreshId();
		SwingThread.invokeLater(() -> {
			model.updateNavigationIndex(id);
		});

		long start = System.currentTimeMillis();
		VirtualFile location = refresh.getRefreshLocation();
		List<VirtualFileWrapper> wrappers = getBrowserContents(location);
		log.trace("refreshBrowser(): $", wrappers);
		long spent = System.currentTimeMillis() - start;

		refresh.refreshCompleted(spent);
		updateJList(id, wrappers);
	}

	private void updateJList(final long id, final List<VirtualFileWrapper> wrappers) {
		SwingThread.invokeLater(() -> {
			if (id != refresh.getRefreshId()) {
				log.trace("skipping update");
				return;
			}
			model.setListData(wrappers, refresh.getPreviousLocation());
		});
	}

	private List<VirtualFileWrapper> getBrowserContents(VirtualFile dir) {
		try {
			List<? extends VirtualFile> files = dir.getChildrenAdvanced().getAlphabeticallyDirectoriesFirst();
			List<VirtualFileWrapper> result = new ArrayList<>(files.size());
			for (int i = 0; i < files.size(); i++) {
				VirtualFile file = files.get(i);
				if (showHiddenFiles.get() || !file.isHidden()) {
					result.add(new VirtualFileWrapper(file));
				}
			}
			return result;

		} catch (RuntimeIOException e) {
			return Collections.emptyList();
		}
	}

	public void shutdown() {
		refresh.setShutdown(true);
	}

	public void showHiddenFiles(boolean value) {
		showHiddenFiles.set(value);
	}
}
