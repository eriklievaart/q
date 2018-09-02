package com.eriklievaart.q.engine;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@SuppressWarnings("unused")
public class FileInvokable implements Invokable {

	@Flag(values = "$dir")
	public void file(VirtualFile file) {
	}

	@Flag(values = "`*.txt`")
	public void wildcard(String glob) {
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
	}

}
