package com.eriklievaart.q.zvariable;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.engine.api.Engine;

public class VariablePlugin implements QPlugin {

	private ServiceCollection<Engine> engine;

	public VariablePlugin(ServiceCollection<Engine> engine) {
		this.engine = engine;
	}

	@Override
	public String getCommandName() {
		return "variable";
	}

	@Override
	public CallPolicy getCallPolicy() {
		return CallPolicy.PIPED;
	}

	@Override
	public Invokable createInstance() {
		return new VariableShellCommand(cmd -> engine.oneCall(s -> s.invoke(cmd)));
	}
}
