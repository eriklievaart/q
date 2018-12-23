package com.eriklievaart.q.engine;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;

public class EmptyInvokable implements Invokable {

	@Override
	public void invoke(PluginContext context) throws Exception {
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
	}
}
