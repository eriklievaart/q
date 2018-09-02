package com.eriklievaart.q.doc.generate;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.reflect.api.LiteralTool;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class ShellCommandScanner {
	private LogTemplate log = new LogTemplate(getClass());

	public Map<String, Class<?>> scan(File dir) {
		log.info("scanning $", dir);
		SystemFile sources = new SystemFile(dir);
		VirtualFileScanner scanner = new VirtualFileScanner(sources);
		scanner.addFileFilter(f -> f.getBaseName().contains("ShellCommand"));

		Map<String, Class<?>> index = new TreeMap<>();

		for (VirtualFile command : scanner) {
			String name = command.getBaseName().toLowerCase().replaceFirst("shellcommand", "");
			String path = sources.getRelativePathOf(command);
			String qualified = toQualifiedName(path);
			Class<?> literal = LiteralTool.getLiteral(qualified);
			if (LiteralTool.isAssignable(literal, Invokable.class)) {
				log.debug("Found ShellCommand $ -> $", name, literal.getName());
				index.put(name, literal);
			}
		}
		return index;
	}

	private static String toQualifiedName(String path) {
		return path.replace('/', '.').replaceFirst(".[^.]++$", "");
	}
}
