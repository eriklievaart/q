package com.eriklievaart.q.zrename;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.zrename.ui.RenameController;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("regex rename files")
public class RenameShellCommand implements Invokable {

	private RenameController controller;
	private VirtualFile directory;

	public RenameShellCommand(RenameController controller) {
		this.controller = controller;
	}

	@Doc("show the UI for renaming files")
	@Flag(values = "$dir", primary = true)
	public void showUi(VirtualFile dir) {
		this.directory = dir;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		controller.showUi(directory);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
	}

}
