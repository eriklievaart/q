package com.eriklievaart.q.zindex;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("lookup locations in the index of recently visited directories")
public class IndexShellCommand implements Invokable {
	private LogTemplate log = new LogTemplate(getClass());

	private final Supplier<QMainUi> ui;
	private final Supplier<UrlResolver> resolver;

	private String location;
	private String protocol;

	public IndexShellCommand(Supplier<QMainUi> ui, Supplier<UrlResolver> resolver) {
		this.ui = ui;
		this.resolver = resolver;
	}

	@Flag(values = "`file`")
	@Doc("specify protocol; uses protocol of active location by default")
	public void protocol(String value) {
		this.protocol = value;
	}

	@Flag(group = "main", values = "`query`", primary = true)
	@Doc("open the closest match in the file browser")
	public void open(String query) {
		this.location = query;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		List<String> result = new IndexMatcher(getDirectoryList()).lookup(location);
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

	private List<String> getDirectoryList() {
		List<String> unfiltered = ui.get().getRecentlyVisitedDirectories();
		if (protocol == null) {
			protocol = ui.get().getQContext().getActive().getDirectory().getUrl().getProtocol();
		}
		return filterDirectories(unfiltered, protocol);
	}

	static List<String> filterDirectories(List<String> all, String protocol) {
		List<String> result = NewCollection.list();

		for (String entry : all) {
			Optional<String> found = UrlTool.getProtocol(entry);

			if (found.isPresent()) {
				if (found.get().equals(protocol)) {
					result.add(entry);
				}
			} else if (Str.isEqualIgnoreCase(protocol, "file")) {
				result.add(entry);
			}
		}
		return result;
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		PluginException.on(Str.isBlank(location), "location to open cannot be blank!");
	}
}