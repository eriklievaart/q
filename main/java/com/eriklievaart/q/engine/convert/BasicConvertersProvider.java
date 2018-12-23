package com.eriklievaart.q.engine.convert;

import java.util.function.Supplier;

import com.eriklievaart.q.engine.convert.construct.SystemFileConstructor;
import com.eriklievaart.q.engine.convert.construct.UriConstructor;
import com.eriklievaart.q.engine.convert.construct.VirtualFileConstructor;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.Converter;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.convert.api.construct.StringConstructor;

/**
 * Guice {@link Provider} for {@link Converter}'s of non-collection types.
 *
 * @author Erik Lievaart
 */
public class BasicConvertersProvider implements Supplier<Converters> {

	private Supplier<UrlResolver> resolver;

	public BasicConvertersProvider(Supplier<UrlResolver> supplier) {
		this.resolver = supplier;
	}

	@Override
	public Converters get() {
		Converter<?> string = new StringConstructor().createConverter();
		Converter<?> uri = new UriConstructor().createConverter();
		Converter<?> file = new SystemFileConstructor(resolver).createConverter();
		Converter<?> vf = new VirtualFileConstructor(resolver).createConverter();

		return new Converters(string, uri, file, vf);
	}
}
