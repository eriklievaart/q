package com.eriklievaart.q.zworkspace;

import java.awt.Component;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class WorkspacePlugin implements QPlugin, QUi {

	private WorkspaceManager manager;

	public WorkspacePlugin(Supplier<QMainUi> supplier, File workspaces) {
		manager = new WorkspaceManager(supplier, workspaces);
	}

	@Override
	public String getCommandName() {
		return "workspace";
	}

	@Override
	public Invokable createInstance() {
		return new WorkspaceShellCommand(manager);
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return null;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Hashtable<String, Consumer<ActionContext>> actions = new Hashtable<>();

		for (int i = 1; i <= 10; i++) {
			final String index = "" + i;
			actions.put("q.workspace." + index, c -> manager.load(index));
		}
		actions.put("q.workspace.other", c -> customWorkspace());
		actions.put("q.workspace.refresh", c -> manager.refresh());

		return actions;
	}

	private void customWorkspace() {
		String input = JOptionPane.showInputDialog("open workspace");
		if (!Str.isBlank(input)) {
			manager.load(input.trim());
		}
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/zworkspace/workspace-bind.txt");
	}
}
