package com.eriklievaart.q.engine.convert.construct;

import java.net.URI;
import java.net.URISyntaxException;

import com.eriklievaart.toolkit.convert.api.construct.AbstractConstructor;

public class UriConstructor extends AbstractConstructor<URI> {

	@Override
	public URI constructObject(final String str) {
		try {
			return new URI(str);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
