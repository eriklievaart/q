package com.eriklievaart.q.laf;

import java.awt.Component;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.LazyMap;
import com.eriklievaart.toolkit.swing.api.laf.LookAndFeel;

public class UiSettings {

	private Map<String, ComponentSettings> settings = new LazyMap<>(missing -> new ComponentSettings());

	public UiSettings(InputStream is) {
		Map<String, Object> config = LookAndFeel.instance().parseConfig(is);
		config.forEach((key, value) -> {
			Check.matches(key, "[^#]++#[^#]++");
			String literal = key.replaceFirst("#.*", "");
			String field = key.replaceFirst("[^#]++#", "");
			settings.get(literal).put(field, value);
		});
	}

	public void apply(Collection<Component> components) {
		for (Component component : components) {
			settings.get(component.getClass().getName()).apply(component);
		}
	}
}
