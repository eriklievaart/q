package com.eriklievaart.q.zindex;

import com.eriklievaart.toolkit.lang.api.ToString;

public class IndexMatch implements Comparable<IndexMatch> {

	public final String url;
	public final IndexMatchType type;

	public IndexMatch(String url, IndexMatchType type) {
		this.url = url;
		this.type = type;
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$:$]", url, type);
	}

	@Override
	public int compareTo(IndexMatch o) {
		if (type == IndexMatchType.PATH && o.type == IndexMatchType.PATH) {
			return url.length() - o.url.length();
		}
		return type.compareTo(o.type);
	}
}