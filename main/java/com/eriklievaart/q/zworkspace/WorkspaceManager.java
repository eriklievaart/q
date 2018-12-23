package com.eriklievaart.q.zworkspace;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.io.api.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class WorkspaceManager {

	private Supplier<QMainUi> ui;

	private File file;
	private String activeWorkspace = "1";
	private Map<String, String> workspaces = NewCollection.hashMap();

	public WorkspaceManager(Supplier<QMainUi> ui, File file) {
		this.ui = ui;
		this.file = file;

		if (file.exists()) {
			workspaces.putAll(PropertiesIO.loadStrings(file));
		}
	}

	public void load(String name) {
		QContext context = ui.get().getQContext();

		workspaces.put(appendLeft(activeWorkspace), context.getLeft().getDirectory().getUrl().getUrlUnescaped());
		workspaces.put(appendRight(activeWorkspace), context.getRight().getDirectory().getUrl().getUrlUnescaped());

		String nameLeft = appendLeft(name);
		if (workspaces.containsKey(nameLeft)) {
			ui.get().navigateFuzzy("left", workspaces.get(nameLeft));
		}
		String nameRight = appendRight(name);
		if (workspaces.containsKey(nameRight)) {
			ui.get().navigateFuzzy("right", workspaces.get(nameRight));
		}
		activeWorkspace = name;
	}

	public void store(String name, VirtualFile left, VirtualFile right) {

		Map<String, String> map = new Hashtable<>();
		if (file.exists()) {
			map.putAll(PropertiesIO.loadStrings(file));
		}
		store(map, appendLeft(name), left.getUrl().getUrlUnescaped());
		store(map, appendRight(name), right.getUrl().getUrlUnescaped());
		PropertiesIO.storeStrings(map, file);
	}

	private void store(Map<String, String> map, String id, String url) {
		map.put(id, url);
		workspaces.put(id, url);
	}

	private String appendLeft(String name) {
		return name + "-1";
	}

	private String appendRight(String name) {
		return name + "-2";
	}
}
