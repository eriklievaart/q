package com.eriklievaart.q.vfs.impl;

import java.util.Map;

import org.junit.Test;

import com.eriklievaart.q.vfs.api.ProtocolResolver;
import com.eriklievaart.q.vfs.protocol.FileProtocolResolver;
import com.eriklievaart.q.vfs.protocol.MemoryProtocolResolver;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.mock.SandboxTest;
import com.eriklievaart.toolkit.reflect.api.InstanceTool;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FuzzyUrlResolverU extends SandboxTest {

	@Test
	public void resolveFullUrl() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///ignore/location");
		start.mkdir();
		VirtualFile destination = memoryFileSystem.resolve("mem:///elsewhere/nested");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "mem:///elsewhere/nested");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///elsewhere/nested");
	}

	@Test
	public void resolveAbsolutePath() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///ignore/location");
		start.mkdir();
		VirtualFile destination = memoryFileSystem.resolve("mem:///elsewhere/nested");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "/elsewhere/nested");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///elsewhere/nested");
	}

	@Test
	public void resolveAbsolutePathFromRoot() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///");
		start.mkdir();
		VirtualFile destination = memoryFileSystem.resolve("mem:///elsewhere/nested");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "/elsewhere/nested");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///elsewhere/nested");
	}

	@Test
	public void resolveExistingChildRelative() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///parent");
		VirtualFile destination = memoryFileSystem.resolve("mem:///parent/child");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "child");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///parent/child");
	}

	@Test
	public void resolveMissingChildRelative() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///start");
		VirtualFile destination = memoryFileSystem.resolve("mem:///start/exists");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "exists/missing");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///start/exists");
	}

	@Test
	public void resolvePartialChildName() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///parent");
		VirtualFile destination = memoryFileSystem.resolve("mem:///parent/child");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "ch");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///parent/child");
	}

	@Test
	public void resolveWithPartialParent() throws Exception {
		VirtualFile destination = systemFile("parent/child");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(systemFile("."), "p/c");
		Check.isTrue(actual.getPath().endsWith("parent/child"), actual.getPath());
	}

	@Test
	public void resolveWildcard() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem:///parent");
		VirtualFile destination = memoryFileSystem.resolve("mem:///parent/child");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "c*d");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem:///parent/child");
	}

	@Test
	public void resolveWindows() throws Exception {
		VirtualFile start = memoryFileSystem.resolve("mem://c:/parent");
		VirtualFile destination = memoryFileSystem.resolve("mem://c:/other");
		destination.mkdir();

		FuzzyUrlResolver fuzzy = new FuzzyUrlResolver(new ExactUrlResolver(createProtocols()));
		VirtualFile actual = fuzzy.resolve(start, "c:/other");
		Check.isEqual(actual.getUrl().getUrlEscaped(), "mem://c:/other");
	}

	private Map<String, ProtocolResolver> createProtocols() {
		MemoryProtocolResolver resolver = new MemoryProtocolResolver();
		InstanceTool.injectFieldByType(resolver, memoryFileSystem);
		return MapTool.of("mem", resolver, "file", new FileProtocolResolver());
	}
}