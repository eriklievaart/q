package com.eriklievaart.q.zfind;

import java.util.regex.Pattern;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.zfind.ui.FindController;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("find files and directories")
public class FindShellCommand implements Invokable {

	private FindController controller;
	private VirtualFile root;
	private String type = null;
	private String include;
	private String exclude;
	private String text;
	private boolean local = false;
	private boolean pathRegex = false;

	public FindShellCommand(FindController controller) {
		this.controller = controller;
	}

	@Flag(group = "root", primary = true, values = "$dir")
	@Doc("Location to find files in. 1 argument: the directory")
	public FindShellCommand root(final VirtualFile dir) {
		root = dir;
		return this;
	}

	@Flag(values = "`FILE`")
	@Doc("find files or directories, valid values: FILE, DIRECTORY, BOTH")
	public FindShellCommand type(String value) {
		type = value;
		return this;
	}

	@Flag(values = "`*`")
	@Doc("Include file names matching grok expression (case insensitive), '|' separates expressions")
	public FindShellCommand include(String value) {
		include = value;
		return this;
	}

	@Flag(values = "``")
	@Doc("Exclude file names matching grok expression (case insensitive), '|' separates expressions")
	public FindShellCommand exclude(String value) {
		exclude = value;
		return this;
	}

	@Flag(values = "``")
	@Doc("Full text search in file contents")
	public FindShellCommand fullTextSearch(String value) {
		text = value;
		return this;
	}

	@Flag
	@Doc("Only find top level files, do not search recursively")
	public FindShellCommand local() {
		local = true;
		return this;
	}

	@Flag
	@Doc("Apply regex against full path instead of only file name")
	public FindShellCommand pathRegex() {
		pathRegex = true;
		return this;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		FileFinder finder = new FileFinder(root);
		configureFinder(context, finder);
		controller.showResults(root, finder.scan());
	}

	private void configureFinder(PluginContext context, FileFinder finder) {
		applyType(finder);
		finder.local(local);
		if (Str.notBlank(include)) {
			finder.include(include);
		}
		if (Str.notBlank(exclude)) {
			finder.exclude(exclude);
		}
		if (Str.notBlank(text)) {
			finder.containsText(text);
		}
		applyRegex(finder, context.getPipedContents());
	}

	private void applyRegex(FileFinder finder, String regex) {
		if (Str.notBlank(regex)) {
			if (pathRegex) {
				finder.regexPath(regex);
			} else {
				finder.regexName(regex);
			}
		}
	}

	private void applyType(FileFinder finder) {
		if (type != null) {
			String upper = type.trim().toUpperCase();
			if (upper.equals("FILE")) {
				finder.filesOnly();
			}
			if (upper.equals("DIRECTORY")) {
				finder.directoriesOnly();
			}
		}
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		String regex = context.getPipedContents();
		if (Str.notBlank(regex)) {
			Pattern.compile(regex);
		}
		if (type != null) {
			Check.matches(type.toUpperCase(), "FILE|DIRECTORY");
		}
	}
}
