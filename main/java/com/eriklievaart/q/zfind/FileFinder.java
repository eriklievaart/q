package com.eriklievaart.q.zfind;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.pattern.WildcardTool;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FileFinder {

	private VirtualFile root;
	private List<Predicate<VirtualFile>> checks = NewCollection.list();
	private boolean local;

	public FileFinder(VirtualFile root) {
		this.root = root;
	}

	public FileFinder filesOnly() {
		checks.add(VirtualFile::isFile);
		return this;
	}

	public FileFinder directoriesOnly() {
		checks.add(VirtualFile::isDirectory);
		return this;
	}

	public FileFinder local(boolean value) {
		local = value;
		return this;
	}

	public FileFinder include(String pattern) {
		Check.notBlank(pattern);
		checks.add(f -> {
			for (String part : pattern.split("\\|")) {
				String grok = part.toLowerCase().trim();
				if (WildcardTool.match(grok, f.getName().toLowerCase().trim())) {
					return true;
				}
			}
			return false;
		});
		return this;
	}

	public FileFinder exclude(String pattern) {
		Check.notBlank(pattern);
		for (String exclude : pattern.split("\\|")) {
			String grok = exclude.toLowerCase().trim();
			checks.add(f -> !WildcardTool.match(grok, f.getName().toLowerCase().trim()));
		}
		return this;
	}

	public FileFinder regexName(String regex) {
		Pattern pattern = Pattern.compile(regex);
		checks.add(f -> pattern.matcher(f.getName()).matches());
		return this;
	}

	public FileFinder regexPath(String regex) {
		Pattern pattern = Pattern.compile(regex);
		checks.add(f -> pattern.matcher(f.getPath()).matches());
		return this;
	}

	public FileFinder containsText(String text) {
		String query = text.toLowerCase();
		checks.add(f -> f.isFile() && f.getContent().readString().toLowerCase().contains(query));
		return this;
	}

	public Iterator<VirtualFile> scan() {
		return local ? FindIterator.local(root, checks) : FindIterator.recursive(root, checks);
	}
}