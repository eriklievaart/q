package com.eriklievaart.q.engine.convert.construct;

import java.util.List;

import com.eriklievaart.toolkit.convert.api.construct.AbstractConstructor;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

@SuppressWarnings("rawtypes")
public class ListConstructor extends AbstractConstructor<List> {

	private final char splitter;

	public ListConstructor(final char splitter) {
		this.splitter = splitter;
	}

	@Override
	public List constructObject(final String str) {
		List<String> list = NewCollection.list();
		for (String item : Str.splitOnChar(str, splitter)) {
			list.add(item.trim());
		}
		return list;
	}

	@Override
	public Class<List> getLiteral() {
		return List.class;
	}
}