package com.eriklievaart.q.zindex;

import java.util.List;
import java.util.function.Supplier;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("lookup locations in the index of recently visited directories")
public class IndexShellCommand implements Invokable {
	private LogTemplate log = new LogTemplate(getClass());

	private final Supplier<QMainUi> ui;
	private final Supplier<UrlResolver> resolver;

	private String location;

	public IndexShellCommand(Supplier<QMainUi> ui, Supplier<UrlResolver> resolver) {
		this.ui = ui;
		this.resolver = resolver;
	}

	@Flag(group = "main", values = "`query`", primary = true)
	@Doc("open the closest match in the file browser")
	public void open(String query) {
		this.location = query;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		List<String> directories = ui.get().getRecentlyVisitedDirectories();
		List<String> result = new IndexMatcher(directories).lookup(location);
		for (int i = 0; i < result.size(); i++) {
			String entry = result.get(i);
			VirtualFile file = resolver.get().resolve(entry);
			if (file.exists() && file.isDirectory()) {
				log.trace("navigating to $", file);
				ui.get().navigateFuzzy("active", entry);
				return;
			}
		}
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		PluginException.on(Str.isBlank(location), "location to open cannot be blank!");
	}
}