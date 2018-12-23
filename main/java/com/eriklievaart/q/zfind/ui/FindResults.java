package com.eriklievaart.q.zfind.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class FindResults {

	private final List<FindResult> results = NewCollection.list();

	public FindResults(List<FindResult> list) {
		this.results.addAll(list);
	}

	public String getFileMessage(String question) {
		StringBuilder builder = new StringBuilder("<html>");
		builder.append(question);
		builder.append("<br/><br/>");
		for (int i = 0; i < 10 && i < results.size(); i++) {
			builder.append(results.get(i).getUrlUnescaped()).append("<br/>");
		}
		if (results.size() > 10) {
			builder.append("...");
		}
		return builder.toString();
	}

	public String getUrls() {
		return String.join(" ", results.stream().map(r -> r.getUrlEscaped()).collect(Collectors.toList()));
	}
}
