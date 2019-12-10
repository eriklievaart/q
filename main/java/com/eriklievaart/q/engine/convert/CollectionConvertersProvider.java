package com.eriklievaart.q.engine.convert;

import java.util.List;
import java.util.function.Supplier;

import com.eriklievaart.q.engine.convert.construct.ListConstructor;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.Converter;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.convert.api.construct.MapConstructor;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

/**
 * Guice {@link Provider} for all available {@link Converter}'s.
 *
 * @author Erik Lievaart
 */
public class CollectionConvertersProvider implements Supplier<Converters> {

	private BasicConvertersProvider basic;

	public CollectionConvertersProvider(Supplier<UrlResolver> supplier) {
		this.basic = new BasicConvertersProvider(supplier);
	}

	@Override
	public Converters get() {
		List<Converter<?>> converters = NewCollection.list();

		converters.addAll(basic.get().listConverters());
		converters.add(new ListConstructor(' ').createConverter());
		converters.add(new MapConstructor().createConverter());

		return new Converters(converters);
	}
}