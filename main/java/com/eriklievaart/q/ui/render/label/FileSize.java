package com.eriklievaart.q.ui.render.label;

public class FileSize {

	private static final long KB = 1024;
	private static final long MB = 1024 * KB;
	private static final long GB = 1024 * MB;

	public static String humanReadableFileSize(long length) {
		if (length > 2 * GB) {
			return length / GB + " GB";
		}
		if (length > 2 * MB) {
			return length / MB + " MB";
		}
		if (length > 2 * KB) {
			return length / KB + " KB";
		}
		return length + " B";
	}

}
