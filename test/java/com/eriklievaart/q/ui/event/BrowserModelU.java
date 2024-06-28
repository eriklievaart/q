package com.eriklievaart.q.ui.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.api.render.VirtualFileWrapper;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;

public class BrowserModelU {

	private MemoryFileSystem memory = new MemoryFileSystem();

	@Test
	public void addOnEmpty() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(ListTool.of(wrapper("ram/bar"), wrapper("ram/foo")), null);
		checkModelContents(components.fileListModel, "bar", "foo");
	}

	@Test
	public void isUpdateRequiredFail() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(ListTool.of(wrapper("ram/bar"), wrapper("ram/foo")), null);
		Check.isFalse(browserModel.isUpdateRequired(Arrays.asList(wrapper("ram/bar"), wrapper("ram/foo"))));
	}

	@Test
	public void isUpdateRequiredFailDuplicate() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		// this can happen with soft links, the link names differ, but the resolved path is the same.
		browserModel.setListData(ListTool.of(wrapper("ram/bar"), wrapper("ram/bar")), null);
		Check.isFalse(browserModel.isUpdateRequired(Arrays.asList(wrapper("ram/bar"), wrapper("ram/bar"))));
	}

	@Test
	public void isUpdateRequiredPassAfterRemove() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(ListTool.of(wrapper("ram/bar"), wrapper("ram/foo")), null);
		Check.isTrue(browserModel.isUpdateRequired(Arrays.asList(wrapper("ram/bar"))));
	}

	@Test
	public void isUpdateRequiredPassAfterAdd() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(ListTool.of(wrapper("ram/bar")), null);
		Check.isTrue(browserModel.isUpdateRequired(Arrays.asList(wrapper("ram/bar"), wrapper("ram/foo"))));
	}

	@Test
	public void deleteElement() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		components.fileListModel.addElement(wrapper("deleteme"));
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(Collections.emptyList(), null);
		checkModelContents(components.fileListModel);
	}

	@Test
	public void replaceElement() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		components.fileListModel.addElement(wrapper("replaceme"));
		BrowserModel browserModel = new BrowserModel(components);

		browserModel.setListData(ListTool.of(wrapper("ram/shiny new toy")), null);
		checkModelContents(components.fileListModel, "shiny new toy");
	}

	@Test
	public void setListDataAddElement() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);
		browserModel.setListData(Arrays.asList(wrapper("ram/b")), null);
		components.fileList.setSelectedIndex(0);

		List<VirtualFileWrapper> elements = ListTool.of(wrapper("ram/a"), wrapper("ram/b"));
		browserModel.setListData(elements, null);
		checkModelContents(components.fileListModel, "a", "b");
		int[] indices = components.fileList.getSelectedIndices();
		Check.isEqual(components.fileList.getSelectedValue().getVirtualFile().getName(), "b");
		Check.isEqual(indices.length, 1);
	}

	@Test
	public void setListDataSelectPrevious() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		List<VirtualFileWrapper> data = ListTool.of(wrapper("ram/a"), wrapper("ram/b"), wrapper("ram/c"));
		browserModel.setListData(data, memory.resolve("ram/b"));
		Check.isEqual(components.fileList.getSelectedValue().getVirtualFile().getName(), "b");
	}

	@Test
	public void setListDataSelectFirst() {
		BrowserComponents components = new BrowserComponents(BrowserOrientation.LEFT);
		BrowserModel browserModel = new BrowserModel(components);

		List<VirtualFileWrapper> data = ListTool.of(wrapper("ram/a"), wrapper("ram/b"), wrapper("ram/c"));
		browserModel.setListData(data, null);
		Check.isEqual(components.fileList.getSelectedValue().getVirtualFile().getName(), "a");
	}

	private void checkModelContents(DefaultListModel<VirtualFileWrapper> model, String... expected) {
		List<String> contents = NewCollection.list();
		for (int i = 0; i < model.size(); i++) {
			contents.add(model.getElementAt(i).toString());
		}
		Assertions.assertThat(contents).containsExactly(expected);
	}

	private VirtualFileWrapper wrapper(String url) {
		return new VirtualFileWrapper(memory.resolve(url));
	}
}