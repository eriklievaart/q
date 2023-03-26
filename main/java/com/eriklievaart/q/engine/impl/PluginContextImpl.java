package com.eriklievaart.q.engine.impl;

import java.util.Map;

import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class PluginContextImpl implements PluginContext {

	private final String swallow;
	private QContext context;
	private VariableResolver resolver;

	public PluginContextImpl(ShellCommand command, QContext context, VariableResolver resolver) {
		Check.noneNull(command, context, resolver);
		this.context = context;
		this.resolver = resolver;
		swallow = command.getSwallowed();
	}

	@Override
	public String getPipedContents() {
		return swallow;
	}

	@Override
	public String getVariable(String variable) {
		return resolver.lookup(variable, context);
	}

	@Override
	public Map<String, String> getVariables() {
		return resolver.createMap(context);
	}
}