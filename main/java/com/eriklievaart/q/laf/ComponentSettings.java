package com.eriklievaart.q.laf;

import java.awt.Component;
import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.reflect.api.InstanceTool;

public class ComponentSettings {

	private Map<String, Object> map = new Hashtable<>();

	public void put(String field, Object value) {
		CheckCollection.notPresent(map, field);
		map.put(field, value);
	}

	public void apply(Component component) {
		InstanceTool.populate(component, map);
	}
}
