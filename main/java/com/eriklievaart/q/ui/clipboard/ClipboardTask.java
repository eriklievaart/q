package com.eriklievaart.q.ui.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * This class defines a single task on or from the system clipboard.
 *
 * @author Erik Lievaart
 */
public class ClipboardTask {
	/** Flavor of the task. */
	public static final DataFlavor CLIPBOARD_TASK_FLAVOR = new DataFlavor(ClipboardTask.class, "ClipboardTask");

	private final String operation;
	private final String urls;

	/**
	 * Constructor.
	 *
	 * @param operation
	 *            The Q command to invoke when copying or moving the files. Should be "copy" or "move".
	 * @param urls
	 *            These URL's should not be escaped and should be separated by the newline character '\n'.
	 */
	ClipboardTask(final String operation, final String urls) {
		this.operation = operation;
		this.urls = urls;
	}

	/**
	 * Constructor.
	 *
	 * @param operation
	 *            The Q command to invoke when copying or moving the files. Should be "copy" or "move".
	 * @param urls
	 *            These URL's should be escaped.
	 */
	public ClipboardTask(final String operation, final List<String> urls) {
		this(operation, unescapeAndJoinLines(urls));
	}

	/**
	 * Constructor for a copy task.
	 */
	ClipboardTask(final List<File> files) {
		this("copy", getFilePaths(files));
	}

	static String getFilePaths(final List<File> files) {
		List<String> paths = NewCollection.list();
		for (File file : files) {
			paths.add(file.getAbsolutePath());
		}
		return StringUtils.join(paths, '\n');
	}

	/**
	 * Get the Q operation to invoke.
	 */
	public String getOperation() {
		return operation;
	}

	String getUrls() {
		return urls;
	}

	static String unescapeAndJoinLines(final List<String> urls) {
		List<String> unescaped = NewCollection.list();
		for (String url : urls) {
			unescaped.add(UrlTool.unescape(url));
		}
		return StringUtils.join(unescaped, '\n');
	}

	/**
	 * List the URL's that are considered to be invalid.
	 */
	public List<String> getInvalidUrls(UrlResolver resolver) {
		List<String> invalid = NewCollection.list();

		for (String url : getUnescapedUrlList()) {
			try {
				if (!resolver.resolve(url).exists()) {
					invalid.add(Str.sub("url does not exist: %", url));
				}
			} catch (RuntimeIOException e) {
				invalid.add(Str.sub("Exception on url % =>", url) + e.getMessage());
			}
		}
		return invalid;
	}

	List<File> getFiles() {
		List<File> list = NewCollection.list();
		if (urls == null) {
			return list;
		}

		for (String url : StringUtils.split(urls, '\n')) {
			list.add(new File(UrlTool.getPath(url)));
		}
		return list;
	}

	/**
	 * Get a List of all URL's in this task with escape sequences.
	 *
	 * @return the escaped URL's in a List.
	 */
	public List<String> getEscapedUrlList() {
		List<String> list = NewCollection.list();
		for (String url : getUnescapedUrlList()) {
			list.add(UrlTool.escape(url));
		}
		return list;
	}

	/**
	 * Get a List of all URL's in this task without escape sequences.
	 *
	 * @return the unescaped URL's as array.
	 */
	private String[] getUnescapedUrlList() {
		if (urls == null) {
			return new String[] {};
		}
		return StringUtils.split(urls, '\n');
	}

	/**
	 * Are there any URL's in this task?
	 */
	public boolean isEmpty() {
		return StringUtils.isEmpty(urls);
	}
}