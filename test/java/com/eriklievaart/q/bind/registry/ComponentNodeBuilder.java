package com.eriklievaart.q.bind.registry;

import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;

import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;

public class ComponentNodeBuilder {

	private final IniNode root;

	public ComponentNodeBuilder(String id) {
		root = new IniNode("component", id);
	}

	public BindingNodeBuilder addBinding() {
		BindingNodeBuilder childBuilder = new BindingNodeBuilder();
		root.addChild(childBuilder.node);
		return childBuilder;
	}

	public InputStream toInputStream() {
		return StreamTool.toInputStream(IniNodeIO.toString(Arrays.asList(root)));
	}

	public static StubUi create(String id, Consumer<BindingNodeBuilder> consumer) {
		ComponentNodeBuilder builder = new ComponentNodeBuilder(id);
		consumer.accept(builder.addBinding());
		return new StubUi(builder.toInputStream());
	}
}
