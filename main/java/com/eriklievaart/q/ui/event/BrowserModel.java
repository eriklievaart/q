package com.eriklievaart.q.ui.event;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.toolkit.vfs.api.VirtualFileComparator;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserModel {

	private BrowserComponents components;
	private final VirtualFileComparator comparator = new VirtualFileComparator();
	long navigationIndex = -1;

	public BrowserModel(BrowserComponents components) {
		this.components = components;
	}

	public void updateNavigationIndex(long id) {
		if (navigationIndex != id) {
			components.fileList.clearSelection();
			components.fileListModel.removeAllElements();
			navigationIndex = id;
		}
	}

	public void setListData(List<VirtualFileWrapper> wrappers, VirtualFile previous) {
		if (components.fileListModel.size() == 0 && wrappers.size() > 0) {
			createNewModel(wrappers);
		} else {
			updateExistingModel(wrappers);
		}
		boolean noSelection = components.fileList.getSelectedIndices().length == 0;
		if (noSelection && wrappers.size() > 0) {
			components.fileList.setSelectedIndex(0);
			selectPreviouslyVisitedLocation(wrappers, previous);
		}
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

	private void updateExistingModel(List<VirtualFileWrapper> wrappers) {
		DefaultListModel<VirtualFileWrapper> model = components.fileListModel;

		int index = 0;
		while (index < wrappers.size()) {
			if (index >= model.getSize()) {
				model.addElement(wrappers.get(index));

			} else {
				VirtualFileWrapper current = wrappers.get(index);
				removeElementsSortedBeforeNextFileAtIndex(current, index);
				add(index, current);
			}
			index++;
		}
		if (model.size() > index) {
			model.removeRange(index, model.getSize() - 1);
		}
	}

	private void add(int index, VirtualFileWrapper current) {
		DefaultListModel<VirtualFileWrapper> model = components.fileListModel;
		ListSelectionModel selectionModel = components.fileList.getSelectionModel();

		// do not add when index exists and files are equal
		if (model.size() <= index || !model.get(index).equals(current)) {
			model.add(index, current);
			if (selectionModel.isSelectedIndex(index)) {
				selectionModel.removeSelectionInterval(index, index);
			}
		}
	}

	private void removeElementsSortedBeforeNextFileAtIndex(VirtualFileWrapper next, int i) {
		while (i < components.fileListModel.size() && compareToIndexInModel(next, i) < 0) {
			components.fileList.removeSelectionInterval(i, i);
			components.fileListModel.remove(i);
		}
	}

	private int compareToIndexInModel(VirtualFileWrapper next, int i) {
		return comparator.compare(components.fileListModel.get(i).getVirtualFile(), next.getVirtualFile());
	}
}
