package com.eriklievaart.q.customize;

import java.awt.Component;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;

public class CustomizeService implements QUi {

	private CustomizeConfig config;

	public CustomizeService(CustomizeConfig config) {
		this.config = config;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return config.getActionMap();
	}

	@Override
	public InputStream getBindings() {
		return config.getBindings();
	}
}
