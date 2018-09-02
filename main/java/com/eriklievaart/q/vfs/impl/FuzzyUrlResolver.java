package com.eriklievaart.q.vfs.impl;

import java.util.Optional;
import java.util.TreeSet;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FuzzyUrlResolver {

	private ExactUrlResolver delegate;

	public FuzzyUrlResolver(ExactUrlResolver delegate) {
		this.delegate = delegate;
	}

	public VirtualFile resolve(VirtualFile start, String location) {
		Optional<String> optional = UrlTool.getProtocol(location);
		boolean hasProtocol = optional.isPresent();
		boolean absolute = location.startsWith("/");
		String protocol = optional.isPresent() ? optional.get() : start.getUrl().getProtocol();

		if (hasProtocol || absolute) {
			return resolveRelative(protocol + ":///", UrlTool.getPath(location).replaceFirst("^[/\\\\]++", ""));

		} else {
			return resolveRelative(start.getUrl().getUrlEscaped(), location);
		}
	}

	public VirtualFile resolveRelative(String base, String path) {
		VirtualFile resolved = resolveFull(base, path);
		if (resolved.exists()) {
			return resolved;
		}
		VirtualFile parent = delegate.resolve(base);
		String head = UrlTool.getHead(path);
		String tail = UrlTool.getTail(path);
		if (head == null) {
			return parent;
		}
		String match = new WildcardResolver(head + "*", getChildNameTreeSet(parent)).wildcardInsensitiveMatch();
		if (match == null) {
			return parent;
		}
		if (tail == null) {
			return parent.resolve(match);
		}
		return resolveRelative(UrlTool.append(base, match), tail);
	}

	private VirtualFile resolveFull(String base, String path) {
		String full = UrlTool.append(base, path);
		VirtualFile resolved = delegate.resolve(full);
		return resolved;
	}

	private TreeSet<String> getChildNameTreeSet(VirtualFile parent) {
		TreeSet<String> names = new TreeSet<>();
		for (VirtualFile child : parent.getChildrenAlphabeticallyDirectoriesFirst()) {
			if (child.exists()) {
				names.add(child.getName());
			}
		}
		return names;
	}
}
