package com.eriklievaart.q.ui.event;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;

import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserModel {
	private LogTemplate log = new LogTemplate(getClass());

	private BrowserComponents components;
	long navigationIndex = -1;
	boolean restoreSelection = false;

	public BrowserModel(BrowserComponents components) {
		this.components = components;
	}

	public void updateNavigationIndex(long id) {
		if (navigationIndex != id) {
			components.fileList.clearSelection();
			restoreSelection = false;
			navigationIndex = id;
		}
	}

	public void setListData(List<VirtualFileWrapper> wrappers, VirtualFile previous) {
		stopwatch(() -> {
			if (isUpdateRequired(wrappers)) {
				log.trace("refreshing JList");
				List<VirtualFileWrapper> selection = components.fileList.getSelectedValuesList();

				createNewModel(wrappers);
				if (restoreSelection) {
					components.fileList.setSelectedIndices(getIndices(selection, wrappers));
				}
				boolean noSelection = components.fileList.getSelectedIndices().length == 0;
				if (noSelection && wrappers.size() > 0) {
					components.fileList.setSelectedIndex(0);
					selectPreviouslyVisitedLocation(wrappers, previous);
				}
				restoreSelection = true;
			}
		});
	}

	private void stopwatch(Runnable run) {
		long start = System.currentTimeMillis();
		run.run();
		long spent = System.currentTimeMillis() - start;
		if (spent > 50) {
			log.trace("update $ms", spent);
		}
	}

	boolean isUpdateRequired(List<VirtualFileWrapper> wrappers) {
		List<String> urls = ListTool.map(wrappers, w -> w.getVirtualFile().getUrl().getUrlUnescaped());
		int size = components.fileListModel.getSize();
		for (int i = 0; i < size; i++) {
			String url = components.fileListModel.get(i).getVirtualFile().getUrl().getUrlUnescaped();
			if (urls.contains(url)) {
				urls.remove(url);
			} else {
				log.trace("$ not in $", url, urls);
				return true;
			}
		}
		return urls.size() != 0;
	}

	private int[] getIndices(List<VirtualFileWrapper> selection, List<VirtualFileWrapper> wrappers) {
		List<Integer> indices = NewCollection.list();
		List<String> names = wrappers.stream().map(w -> w.getVirtualFile().getName()).collect(Collectors.toList());
		for (VirtualFileWrapper wrapper : selection) {
			int index = names.indexOf(wrapper.getVirtualFile().getName());
			if (index >= 0) {
				indices.add(index);
			}
		}
		int[] primitives = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			primitives[i] = indices.get(i);
		}
		return primitives;
	}

	private void selectPreviouslyVisitedLocation(List<VirtualFileWrapper> wrappers, VirtualFile previous) {
		if (previous != null) {
			String url = previous.getUrl().getUrlUnescaped();
			for (int i = 0; i < wrappers.size(); i++) {
				if (wrappers.get(i).getVirtualFile().getUrl().getUrlUnescaped().equals(url)) {
					components.fileList.setSelectedValue(wrappers.get(i), true);
					return;
				}
			}
		}
	}

	/**
	 * Create a new {@link DefaultListModel} to prevent individual updates.
	 */
	private void createNewModel(List<VirtualFileWrapper> wrappers) {
		DefaultListModel<VirtualFileWrapper> model = new DefaultListModel<>();
		for (VirtualFileWrapper wrapper : wrappers) {
			model.addElement(wrapper);
		}
		components.fileListModel = model;
		components.fileList.setModel(model);
	}
}