package com.eriklievaart.q.engine.convert.construct;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.construct.AbstractConstructor;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class SystemFileConstructor extends AbstractConstructor<File> {

	private Supplier<UrlResolver> resolver;

	public SystemFileConstructor(Supplier<UrlResolver> resolver) {
		this.resolver = resolver;
	}

	@Override
	public File constructObject(final String str) {
		Optional<String> protocol = UrlTool.getProtocol(str);
		Check.isTrue(protocol.isEmpty() || protocol.get().equals("file"), "invalid system file: %", str);

		VirtualFile vf = resolver.get().resolve(str);
		if (vf instanceof SystemFile) {
			SystemFile sf = (SystemFile) vf;
			return sf.unwrap();
		}
		throw new FormattedException("Not a local file: %", str);
	}
}