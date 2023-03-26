package com.eriklievaart.q.zindex;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class IndexMatcher {

	private List<String> urls;

	public IndexMatcher(List<String> urls) {
		this.urls = urls;
	}

	public List<String> lookup(String location) {
		List<IndexMatch> matches = allMatches(location);
		Collections.sort(matches);
		List<IndexMatch> limited = ListTool.limitSize(matches, 100);
		return ListTool.map(limited, m -> m.url);
	}

	private List<IndexMatch> allMatches(String location) {
		List<IndexMatch> matches = NewCollection.list();
		for (String url : urls) {
			IndexMatch match = getMatch(location, url);
			if (match != null) {
				matches.add(match);
			}
		}
		return matches;
	}

	private IndexMatch getMatch(String location, String url) {
		Optional<IndexMatch> exact = getExactMatch(location, url);
		if (exact.isPresent()) {
			return exact.get();
		}
		Optional<IndexMatch> start = getStartMatch(location, url);
		if (start.isPresent()) {
			return start.get();
		}
		return contains(location, url).orElse(getPathMatch(location, url).orElse(null));
	}

	private Optional<IndexMatch> getExactMatch(String location, String url) {
		String name = UrlTool.getName(url);
		if (name.equals(location)) {
			return Optional.of(new IndexMatch(url, IndexMatchType.EXACT));
		}
		if (name.toUpperCase().equals(location.toUpperCase())) {
			return Optional.of(new IndexMatch(url, IndexMatchType.EXACT_INSENSITIVE));
		}
		return Optional.empty();
	}

	private Optional<IndexMatch> getStartMatch(String location, String url) {
		String name = UrlTool.getName(url);
		if (name.startsWith(location)) {
			return Optional.of(new IndexMatch(url, IndexMatchType.STARTS));
		}
		if (name.toUpperCase().startsWith(location.toUpperCase())) {
			return Optional.of(new IndexMatch(url, IndexMatchType.STARTS_INSENSITIVE));
		}
		return Optional.empty();
	}

	private Optional<IndexMatch> contains(String location, String url) {
		if (UrlTool.getName(url).contains(location)) {
			return Optional.of(new IndexMatch(url, IndexMatchType.CONTAINS));
		}
		if (UrlTool.getName(url).toUpperCase().contains(location.toUpperCase())) {
			return Optional.of(new IndexMatch(url, IndexMatchType.CONTAINS_INSENSITIVE));
		}
		return Optional.empty();
	}

	private Optional<IndexMatch> getPathMatch(String location, String url) {
		int index = 0;
		String[] words = location.split("[ /]");
		for (String word : words) {
			index = url.substring(index).indexOf(word);
			if (index < 0) {
				return Optional.empty();
			}
		}
		return Optional.of(new IndexMatch(url, IndexMatchType.PATH));
	}
}