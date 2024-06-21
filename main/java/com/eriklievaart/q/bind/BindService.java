package com.eriklievaart.q.bind;

import java.awt.Component;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.parse.ConfigParser;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;

/**
 * register user bindings
 */
public class BindService implements QUi {

	private File bindingFile;
	private File actionFile;
	private Supplier<Engine> engine;

	public BindService(File actions, File bindings, Supplier<Engine> engine) {
		this.actionFile = actions;
		this.bindingFile = bindings;
		this.engine = engine;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return null;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, String> raw = PropertiesIO.loadStrings(actionFile);
		Map<String, String> user = MapTool.mapKeys(raw, key -> "user." + key);
		return MapTool.mapValues(user, code -> createConsumer(code));
	}

	private Consumer<ActionContext> createConsumer(String string) {
		return ctx -> engine.get().invokeTemplated(string);
	}

	@Override
	public InputStream getBindings() {
		validateBindings();
		return FileTool.toInputStream(bindingFile);
	}

	private void validateBindings() {
		try {
			new ConfigParser().parse(0L, FileTool.toInputStream(bindingFile));
		} catch (Exception e) {
			System.err.println("*ERROR*: invalid user binding file: " + bindingFile);
			e.printStackTrace();
			System.exit(500);
		}
	}

	public boolean isConfigured() {
		return bindingFile.isFile() || actionFile.isFile();
	}
}
