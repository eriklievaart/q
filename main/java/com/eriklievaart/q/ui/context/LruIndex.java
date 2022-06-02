package com.eriklievaart.q.ui.context;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class LruIndex {
	private static final int MAX_LINES = 1000;
	private LogTemplate log = new LogTemplate(getClass());

	private File file;
	private List<String> index = NewCollection.concurrentList();

	public LruIndex(File file) {
		this.file = file;
		index.addAll(load());
		log.info("LRU index file($) $", index.size(), file.getAbsolutePath());
	}

	public void add(String path) {
		boolean recent = isRecent(path);

		index.remove(path);
		index.add(path);

		if (!recent) {
			store();
		}
	}

	private boolean isRecent(String url) {
		return ListTool.subList(getRecentlyVisited(), 0, 10).contains(url);
	}

	private List<String> load() {
		if (file.exists()) {
			return FileTool.readLines(file);
		} else {
			log.warn("File does not exist: $", file);
			return Collections.emptyList();
		}
	}

	private void store() {
		for (String line : load()) {
			if (!index.contains(line)) {
				index.add(line);
			}
		}
		FileTool.writeLines(file, getLines());
	}

	private List<String> getLines() {
		if (index.size() < MAX_LINES) {
			return index;
		}
		List<String> list = NewCollection.list();
		for (int i = 0; i < index.size(); i++) {
			list.add(index.get(i));
		}
		return list;
	}

	public List<String> getRecentlyVisited() {
		return ListTool.reversedCopy(index);
	}
}