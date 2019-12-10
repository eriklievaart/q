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
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserObserver {
	private static final int REFRESH_SLEEP_TIME = 100;

	private LogTemplate log = new LogTemplate(getClass());

	private BrowserModel model;
	private BrowserComponents components;
	private BrowserRefresh refresh = new BrowserRefresh();
	private List<VirtualFile> history = NewCollection.list();
	private List<VirtualFile> revisit = NewCollection.list();
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
		if (dir.isDirectory()) {
			history.add(dir);
			revisit.clear();
			open(dir);
		}
	}

	public void openPrevious() {
		if (history.size() > 1) {
			revisit.add(history.remove(history.size() - 1));
			VirtualFile previous = history.get(history.size() - 1);
			if (previous.isDirectory()) {
				open(previous);
			} else {
				openPrevious();
			}
		}
	}

	public void openRedoHistory() {
		if (revisit.isEmpty()) {
			return;
		}
		VirtualFile file = revisit.remove(revisit.size() - 1);
		if (file.isDirectory()) {
			history.add(file);
			open(file);
		} else {
			openRedoHistory();
		}
	}

	private void open(VirtualFile dir) {
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
					Thread.sleep(REFRESH_SLEEP_TIME);
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
		long spent = System.currentTimeMillis() - start;

		refresh.refreshCompleted(spent);
		updateJList(id, wrappers);
	}

	private void updateJList(final long id, final List<VirtualFileWrapper> wrappers) {
		try {
			SwingThread.invokeAndWait(() -> {
				if (id != refresh.getRefreshId()) {
					log.trace("skipping update");
					return;
				}
				model.setListData(wrappers, refresh.getPreviousLocation());
			});
		} catch (Exception e) {
			log.warn(e);
		}
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