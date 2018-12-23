package com.eriklievaart.q.ui.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Custom Transferable for copying a List of URL's to the system clipboard.
 * 
 * @author Erik Lievaart
 */
public class UrlTransferable implements Transferable {

	private final ClipboardTask task;

	/**
	 * Constructor.
	 * 
	 * @param task
	 *            details of the Transferable.
	 */
	public UrlTransferable(final ClipboardTask task) {
		this.task = task;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		if (task.isEmpty()) {
			return new DataFlavor[] { DataFlavor.stringFlavor };
		}
		return new DataFlavor[] { DataFlavor.stringFlavor, DataFlavor.javaFileListFlavor,
				ClipboardTask.CLIPBOARD_TASK_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		if (!task.isEmpty()) {
			if (ClipboardTask.CLIPBOARD_TASK_FLAVOR.equals(flavor) || DataFlavor.javaFileListFlavor.equals(flavor)) {
				return true;
			}
		}
		return DataFlavor.stringFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DataFlavor.javaFileListFlavor.equals(flavor)) {
			return task.getFiles();
		}
		if (flavor.equals(DataFlavor.stringFlavor)) {
			return task.getUrls();
		}
		if (flavor.equals(ClipboardTask.CLIPBOARD_TASK_FLAVOR)) {
			return task;
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
