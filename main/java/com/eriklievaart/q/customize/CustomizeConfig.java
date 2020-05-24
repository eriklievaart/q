package com.eriklievaart.q.customize;

import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;
import com.eriklievaart.toolkit.lang.api.IdGenerator;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class CustomizeConfig {

	private IdGenerator ids = new IdGenerator();
	private ServiceCollection<Engine> engine;
	private Map<String, Consumer<ActionContext>> actions = new Hashtable<>();
	private String bindings;

	public CustomizeConfig(File file, ServiceCollection<Engine> engine) {
		this.engine = engine;
		List<IniNode> nodes = IniNodeIO.read(file);
		nodes.forEach(this::createActions);
		bindings = IniNodeIO.toString(nodes);
	}

	private void createActions(IniNode node) {
		if (node.hasProperty("engine")) {
			Check.isFalse(node.hasProperty("action"), "delete either 'action' or 'engine' on node: $", node);
			String id = "customize." + ids.next();
			node.setProperty("action", id);
			String command = node.deleteProperty("engine");
			actions.put(id, ctx -> engine.oneCall(s -> s.invokeTemplated(command)));

		} else {
			node.getChildren().forEach(this::createActions);
		}
	}

	public Map<String, Consumer<ActionContext>> getActionMap() {
		return new Hashtable<>(actions);
	}

	public InputStream getBindings() {
		return StreamTool.toInputStream(bindings);
	}
}
