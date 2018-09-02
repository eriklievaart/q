package com.eriklievaart.q.api.engine;

import java.util.Collections;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class DummyPluginContext implements PluginContext {

	private String pipedContents;
	private Map<String, String> variables = NewCollection.map();

	public void setPipedContents(String pipedContents) {
		this.pipedContents = pipedContents;
	}

	@Override
	public String getPipedContents() {
		return pipedContents;
	}

	public void put(String key, String value) {
		variables.put(key, value);
	}

	@Override
	public String getVariable(String key) {
		return variables.get(key);
	}

	@Override
	public Map<String, String> getVariables() {
		return Collections.unmodifiableMap(variables);
	}

}
