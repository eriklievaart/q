package com.eriklievaart.q.ui.event;

import javax.swing.JList;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class FileListKeyEventU {
	@Test
	public void characterSelectFirstItem() {
		JList<String> list = new JList<>(new String[] { "a", "b", "c" });
		new FileListKeyEvent().character('a', list);
		Check.isEqual(list.getSelectedValue(), "a");
	}

	@Test
	public void characterSelectMiddleItem() {
		JList<String> list = new JList<>(new String[] { "a", "b", "c" });
		new FileListKeyEvent().character('b', list);
		Check.isEqual(list.getSelectedValue(), "b");
	}

	@Test
	public void characterSelectLastItem() {
		JList<String> list = new JList<>(new String[] { "a", "b", "c" });
		new FileListKeyEvent().character('c', list);
		Check.isEqual(list.getSelectedValue(), "c");
	}

	@Test
	public void characterSelectAfterLastItem() {
		JList<String> list = new JList<>(new String[] { "a", "b", "c" });
		new FileListKeyEvent().character('d', list);
		Check.isEqual(list.getSelectedValue(), "c");
	}

	@Test
	public void characterSelectFirstMatch() {
		JList<String> list = new JList<>(new String[] { "aa", "ab", "ba", "bb" });
		new FileListKeyEvent().character('b', list);
		Check.isEqual(list.getSelectedValue(), "ba");
	}

	@Test
	public void characterSelectMultipleCharacters() {
		JList<String> list = new JList<>(new String[] { "a", "aa", "ab", "b", "ba", "bb" });
		FileListKeyEvent event = new FileListKeyEvent();
		event.character('b', list);
		Check.isEqual(list.getSelectedValue(), "b");
		event.character('a', list);
		Check.isEqual(list.getSelectedValue(), "ba");
	}

	@Test
	public void characterSelectFileAfterDirectory() {
		String[] data = new String[] { "data", "doc", "src", "customize.txt", "files.log", "logging.ini" };
		JList<String> list = new JList<>(data);
		new FileListKeyEvent().character('f', list);
		Check.isEqual(list.getSelectedValue(), "files.log");
	}

	@Test
	public void characterSelectFileAfterDirectoryTwoLetters() {
		String[] data = new String[] { "data", "doc", "src", "dummy.txt", "files.log", "logging.ini" };
		JList<String> list = new JList<>(data);

		FileListKeyEvent event = new FileListKeyEvent();
		event.character('d', list);
		Check.isEqual(list.getSelectedValue(), "data");
		event.character('u', list);
		Check.isEqual(list.getSelectedValue(), "dummy.txt");
	}
}
