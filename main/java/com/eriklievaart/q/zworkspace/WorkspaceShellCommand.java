package com.eriklievaart.q.zworkspace;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("Switch between multiple workspaces")
public class WorkspaceShellCommand implements Invokable {

	private WorkspaceManager manager;

	private WorkspaceCommandType command;
	private String name;
	private VirtualFile left;
	private VirtualFile right;

	public WorkspaceShellCommand(WorkspaceManager manager) {
		this.manager = manager;
	}

	@Doc("load specified workspace")
	@Flag(group = "main", values = { "`1`" }, primary = true)
	public void load(String workspace) {
		this.command = WorkspaceCommandType.LOAD;
		this.name = workspace;
	}

	@Doc("reload current workspace")
	@Flag(group = "main")
	public void refresh() {
		this.command = WorkspaceCommandType.REFRESH;
	}

	@Doc("persist default directories for workspace")
	@Flag(group = "main", values = { "`1`", "$dir1", "$dir2" })
	public void store(String workspace, VirtualFile leftDir, VirtualFile rightDir) {
		this.command = WorkspaceCommandType.STORE;
		this.name = workspace;
		this.left = leftDir;
		this.right = rightDir;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		switch (command) {

		case LOAD:
			manager.load(name);
			return;

		case STORE:
			manager.store(name, left, right);
			return;

		case REFRESH:
			manager.refresh();
			return;
		}
		throw new FormattedException("Unknown command type %", command);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		if (command == WorkspaceCommandType.STORE) {
			Check.isTrue(left.isDirectory(), "not a directory: %", left);
			Check.isTrue(right.isDirectory(), "not a directory: %", right);
		}
	}

	private enum WorkspaceCommandType {
		STORE, LOAD, REFRESH
	}
}
