package com.eriklievaart.q.znew;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("create files and directories in any writable filesystem (note: can create nested paths)")
class NewShellCommand implements Invokable {

	private VirtualFile dir;
	private String create;
	private Boolean file;

	@Flag(group = "main", values = { "$dir", "`file`" })
	@Doc("Create a file. Two arguments: 1) destination dir 2) file name")
	public NewShellCommand file(final VirtualFile parent, final String name) {
		this.dir = parent;
		this.create = name;
		file = Boolean.TRUE;
		return this;
	}

	@Flag(group = "main", values = { "$dir", "`directory`" }, primary = true)
	@Doc("Create a directory. Two arguments: 1) destination dir 2) directory name")
	public NewShellCommand directory(final VirtualFile parent, final String name) {
		this.dir = parent;
		this.create = name;
		file = Boolean.FALSE;
		return this;
	}

	@Override
	@Doc("piped contents will be written to the file (file flag only)")
	public void invoke(PluginContext context) throws Exception {
		VirtualFile target = dir.resolve(create);

		if (file) {
			target.createFile();
			String contents = context.getPipedContents();
			if (!Str.isBlank(contents)) {
				target.getContent().writeString(contents);
			}

		} else {
			target.mkdir();
		}
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		if (Str.notBlank(context.getPipedContents())) {
			Check.isTrue(file, "Can only pipe file contents when -file flag is set");
		}
		Check.isTrue(!dir.exists() || dir.isDirectory(), "Directory not writable %", dir);
	}
}