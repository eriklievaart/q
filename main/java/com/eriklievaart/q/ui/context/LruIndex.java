package com.eriklievaart.q.ui.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class LruIndex {
	private static final int MAX_LINES = 1000;
	private LogTemplate log = new LogTemplate(getClass());

	private File file;
	private List<String> index = NewCollection.concurrentList();

	public LruIndex(File file) {
		this.file = file;
		load();
	}

	public void add(String url) {
		boolean recent = isRecent(url);

		index.remove(url);
		index.add(0, url);

		if (!recent) {
			store();
		}
	}

	private boolean isRecent(String url) {
		for (int i = 0; i < 10 && i < index.size(); i++) {
			if (index.get(i).equals(url)) {
				return true;
			}
		}
		return false;
	}

	private void load() {
		if (file.exists()) {
			index = FileTool.readLines(file);
		} else {
			log.warn("File does not exist: $", file);
		}
	}

	private void store() {
		FileTool.writeLines(file, getLines());
	}

	private List<String> getLines() {
		if (index.size() < MAX_LINES) {
			return index;
		}
		List<String> list = NewCollection.list();
		for (int i = 0; i < index.size(); i++) {
			String line = index.get(i);
			if (!line.startsWith("mem://")) {
				list.add(line);
			}
		}
		return list;
	}

	public List<String> getRecentlyVisited() {
		return new ArrayList<>(index);
	}
}
