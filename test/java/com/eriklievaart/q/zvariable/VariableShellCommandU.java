package com.eriklievaart.q.zvariable;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.eriklievaart.q.api.engine.DummyPluginContext;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class VariableShellCommandU {

	@Test
	public void noop() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.setPipedContents("nothing to do");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "nothing to do");
	}

	@Test
	public void single() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("url", "file:///tmp/foo.txt");
		context.setPipedContents("execute -f | echo @{url}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo file:///tmp/foo.txt");
	}

	@Test
	public void multiple() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("urls", "file:///tmp/foo.txt file:///tmp/bar.txt");
		context.setPipedContents("execute -f | echo @{urls}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo file:///tmp/foo.txt file:///tmp/bar.txt");
	}

	@Test
	public void attribute() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("url", "file:///tmp/foo.txt");
		context.setPipedContents("execute -f | echo @{url:path}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo /tmp/foo.txt");
	}

	@Test
	public void escape() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("urls", "file:///tmp/with%20space.txt file:///tmp/without-space.txt");
		context.setPipedContents("execute -f | echo @{urls/}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo file:///tmp/with%20space.txt file:///tmp/without-space.txt");
	}

	@Test
	public void unescape() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("urls", "file:///tmp/with%20space.txt file:///tmp/without-space.txt");
		context.setPipedContents("execute -f | echo @{urls}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo file:///tmp/with space.txt file:///tmp/without-space.txt");
	}

	@Test
	public void quote() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("urls", "file:///tmp/with%20space.txt file:///tmp/with'quote.txt");
		context.setPipedContents("execute -f | echo @{urls'}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo 'file:///tmp/with space.txt' 'file:///tmp/with\\'quote.txt'");
	}

	@Test
	public void attributeEscaped() throws Exception {
		DummyPluginContext context = new DummyPluginContext();
		context.put("url", "file:///tmp/file%20with'quote.txt");
		context.setPipedContents("execute -f | echo @{url:name'}");

		AtomicReference<String> result = new AtomicReference<>();
		new VariableShellCommand(result::set).invoke(context);
		Check.isEqual(result.get(), "execute -f | echo 'file with\\'quote.txt'");
	}
}
