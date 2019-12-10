package com.eriklievaart.q.ui.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

import com.eriklievaart.toolkit.io.api.SystemClipboard;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

/**
 * Class responsible for writing and reading file operations to the system clipboard.
 *
 * @author Erik Lievaart
 */
public class ClipboardManager {
	private static LogTemplate log = new LogTemplate(ClipboardManager.class);

	/**
	 * Copy the specified URL's to the clipboard.
	 */
	public static void copyUrls(final List<String> urls) {
		log.debug("copying urls $", urls);
		SystemClipboard.writeTransferable(new UrlTransferable(new ClipboardTask("copy", urls)));
	}

	/**
	 * Cut the specified URL's to the clipboard.
	 */
	public static void cutUrls(final List<String> urls) {
		log.debug("cutting urls $", urls);
		SystemClipboard.writeTransferable(new UrlTransferable(new ClipboardTask("move", urls)));
	}

	/**
	 * Get the task currently on the clipboard.
	 */
	public static ClipboardTask getClipboardTask() {
		ClipboardTask task = SystemClipboard.readFlavor(ClipboardTask.CLIPBOARD_TASK_FLAVOR);
		if (task != null) {
			log.trace("pasting urls: $", task.getUrls());
			return task;
		}
		List<File> files = SystemClipboard.readFlavor(DataFlavor.javaFileListFlavor);
		if (files != null) {
			log.trace("pasting files: $", files);
			return new ClipboardTask(files);
		}
		String text = SystemClipboard.readString();
		log.trace("pasting text: $", text);
		return new ClipboardTask("copy", text);
	}
}