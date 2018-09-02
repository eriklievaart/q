package com.eriklievaart.q.engine.impl;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.convert.CollectionConvertersProvider;
import com.eriklievaart.q.vfs.impl.UrlResolverService;
import com.eriklievaart.toolkit.convert.api.ConversionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.reflect.api.FieldTool;
import com.eriklievaart.toolkit.reflect.api.GenericsTool;

public class PluginRunnerU {

	private PluginRunner testable;

	@Before
	public void init() {
		CollectionConvertersProvider converters = new CollectionConvertersProvider(() -> new UrlResolverService());
		testable = new PluginRunner(new PluginIndex(), converters);
	}

	@Test
	public void convertValueBasic() throws ConversionException {
		class Local {
			@SuppressWarnings("unused")
			private URI uri;
		}
		Type type = FieldTool.getField(Local.class, "uri").getGenericType();

		URI uri = (URI) testable.convertValue(type, "http://www.google.com");
		Check.isEqual(uri.getHost(), "www.google.com");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertValueList() throws Exception {
		class Local {
			@SuppressWarnings("unused")
			private List<URI> list;
		}
		Type type = FieldTool.getField(Local.class, "list").getGenericType();
		Type generic = GenericsTool.getGenericType(type);
		Check.isEqual(generic, URI.class);

		List<URI> uriList = (List<URI>) testable.convertValue(type, "http://www.google.com");

		CheckCollection.isSize(uriList, 1);
		Check.isEqual(uriList.get(0), new URI("http://www.google.com"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertValueMap() throws Exception {
		class Local {
			@SuppressWarnings("unused")
			private Map<String, String> map;
		}
		Type type = FieldTool.getField(Local.class, "map").getGenericType();
		Check.isEqual(GenericsTool.getLiteral(type), Map.class);

		Map<String, String> map = (Map<String, String>) testable.convertValue(type, "key1=value1#key2=value2");

		CheckCollection.isSize(map, 2);
		Check.isEqual(map.get("key1"), "value1");
	}

	@Test
	public void callFlagsPrimitive() {
		final AtomicReference<URI> reference = new AtomicReference<>();
		class Local {
			@Flag
			public void flag(final URI uri) {
				reference.set(uri);
			}
		}
		testable.callFlags(new Local(), MapTool.of("flag", new String[] { "http://www.google.com" }));

		URI called = reference.get();
		Check.isEqual(called.getHost(), "www.google.com");
	}

	@Test
	public void callFlagsGenericList() {
		final AtomicReference<List<URI>> reference = new AtomicReference<>();
		class Local {
			@Flag
			public void flag(final List<URI> list) {
				reference.set(list);
			}
		}
		testable.callFlags(new Local(), MapTool.of("flag", new String[] { "http://www.google.com" }));

		List<URI> called = reference.get();
		CheckCollection.notEmpty(called);
		CheckCollection.isSize(called, 1);
		Check.isEqual(called.get(0).getHost(), "www.google.com");
	}

}
