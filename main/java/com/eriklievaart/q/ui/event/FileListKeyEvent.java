package com.eriklievaart.q.ui.event;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JList;

import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class FileListKeyEvent {

	private String buffer = "";
	private AtomicLong timestamp = new AtomicLong();

	public void character(char c, JList<?> list) {
		if (!isSearchQuery(c)) {
			buffer = "";
			return;
		}
		updateBufferedQuery(c);
		list.setSelectedIndex(search(buffer.toUpperCase(), convertToStrings(list)));
	}

	private void updateBufferedQuery(char c) {
		if (System.currentTimeMillis() - timestamp.get() > 1000) {
			buffer = "";
		}
		timestamp.set(System.currentTimeMillis());
		buffer += c;
	}

	private boolean isSearchQuery(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}

	private List<String> convertToStrings(JList<?> list) {
		List<String> strings = NewCollection.list();
		for (int i = 0; i < list.getModel().getSize(); i++) {
			strings.add(list.getModel().getElementAt(i).toString().toUpperCase());
		}
		return strings;
	}

	private int search(String query, List<String> list) {
		int starts = searchForStartsWith(query, list);
		return starts != -1 ? starts : searchForApproximation(query, list);
	}

	private int searchForStartsWith(String query, List<String> list) {
		final AtomicInteger result = new AtomicInteger(-1);
		ListTool.iterate(list, (index, element) -> {
			if (element.startsWith(query)) {
				result.compareAndSet(-1, index);
			}
		});
		return result.get();
	}

	private int searchForApproximation(String query, List<String> list) {
		Console.println("searching for % in $", query, list);
		int select = 0;
		while (select < list.size() - 1) {
			int compare = query.compareTo(list.get(select));
			Console.println("compare to $: $", list.get(select), compare);
			if (compare <= 0) {
				break;
			}
			select++;
		}
		System.out.println("selected: " + list.get(select));
		return select;
	}
}
