package com.eriklievaart.q.engine;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QContextFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.test.api.BombSquad;
import com.eriklievaart.toolkit.test.api.SandboxTest;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class EngineServiceI extends SandboxTest {

	@Test
	public void invokePipedJobPass() throws Exception {
		AtomicReference<String> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Override
			public void invoke(PluginContext context) throws Exception {
				result.set(context.getPipedContents());
			}
		});
		invokeOnEngine(plugin, new QContextFactory().make(), "d|hut");
		Check.isEqual(result.get(), "hut");
	}

	@Test
	public void invokePipedJobForFlagsOnly() throws Exception {

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable());
		plugin.setCallPolicy(CallPolicy.FLAGS_ONLY);
		BombSquad.diffuse(RuntimeException.class, "does not accept piped input", () -> {
			invokeOnEngine(plugin, new QContextFactory().make(), "d|hut");
		});
	}

	@Test
	public void invokeJobWithFlags() throws Exception {
		AtomicReference<String> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			private String flag = "none";

			@Flag(group = "main")
			public void alpha() {
				flag = "alpha";
			}

			@Flag(group = "main")
			public void beta() {
				flag = "beta";
			}

			@Override
			public void invoke(PluginContext context) throws Exception {
				result.set(flag);
			}
		});
		QContext context = new QContextFactory().make();
		invokeOnEngine(plugin, context, "d");
		Check.isEqual(result.get(), "none");
		invokeOnEngine(plugin, context, "d -a");
		Check.isEqual(result.get(), "alpha");
		invokeOnEngine(plugin, context, "d -b");
		Check.isEqual(result.get(), "beta");
	}

	@Test
	public void invokeJobWithPrimaryFlag() throws Exception {
		AtomicReference<String> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", primary = true, values = "``")
			public void flag(String value) {
				result.set(value);
			}

			@Override
			public void invoke(PluginContext context) throws Exception {
			}
		});
		QContext context = new QContextFactory().make();
		invokeOnEngine(plugin, context, "d `blabla`");
		Check.isEqual(result.get(), "blabla");
	}

	@Test
	public void invokeJobWithVariableAutoResolve() throws Exception {
		AtomicReference<VirtualFile> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", values = "$dir2")
			public void flag(VirtualFile file) {
				result.set(file);
			}
		});
		QContext context = new QContextFactory().rightDir("snowden").make();
		invokeOnEngine(plugin, context, "d -f");
		Check.isEqual(result.get().getPath(), "/snowden");
	}

	@Test
	public void invokeJobWithFile() throws Exception {
		AtomicReference<File> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", values = "$dir1", primary = true)
			public void flag(File file) {
				result.set(file);
			}
		});
		File expected = file("pimp");
		QContext context = new QContextFactory().leftSystemDir(expected.getAbsolutePath()).make();
		invokeOnEngine(plugin, context, "dummy");
		Check.isEqual(result.get().getPath(), expected.getAbsolutePath());
	}

	@Test
	public void invokeJobWithFileEscapeCharacters() throws Exception {
		AtomicReference<File> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", values = "``")
			public void flag(File file) {
				result.set(file);
			}
		});
		invokeOnEngine(plugin, new QContextFactory().make(), "d -f `/tmp/with%20space`");
		Check.isEqual(result.get().getPath(), "/tmp/with space");
	}

	@Test
	public void invokeJobWithListEscapeCharacters() throws Exception {
		AtomicReference<List<String>> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", values = "``")
			public void flag(List<String> arg) {
				result.set(arg);
			}
		});
		invokeOnEngine(plugin, new QContextFactory().make(), "d -f `a%20b a b`");
		Assertions.assertThat(result.get()).containsExactly("a b", "a", "b");
	}

	@Test
	public void invokeJobWithListGeneric() throws Exception {
		AtomicReference<List<VirtualFile>> result = new AtomicReference<>();

		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(group = "main", values = "``")
			public void flag(List<VirtualFile> arg) {
				result.set(arg);
			}
		});
		invokeOnEngine(plugin, new QContextFactory().make(), "d -f `/tmp /tmp`");
		Assertions.assertThat(result.get()).containsExactly(new SystemFile("/tmp"), new SystemFile("/tmp"));
	}

	@Test
	public void parseMissingFlag() throws Exception {
		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag
			public void banana() {
			}
		});
		EngineResult result = parse(plugin, "dummy -");
		Check.isTrue(result.isError());
		Assertions.assertThat(result.getMessage()).contains("[banana]");
	}

	@Test
	public void parseFlagSuppliedButDoesNotExist() throws Exception {
		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable());
		EngineResult result = parse(plugin, "dummy -f");
		Check.isTrue(result.isError());
		Assertions.assertThat(result.getMessage()).isEqualTo("dummy -f -> Unknown flag `f`");
	}

	@Test
	public void parseValidationFailure() throws Exception {
		DummyPlugin plugin = new DummyPlugin("dummy", new EmptyInvokable() {
			@Flag(primary = true)
			public void banana() {
			}

			@Override
			public void validate(PluginContext context) throws PluginException {
				throw new PluginException("kiekaboo!");
			}
		});
		EngineResult result = parse(plugin, "dummy");
		Check.isTrue(result.isError());
		Assertions.assertThat(result.getMessage()).isEqualTo("dummy -banana -> kiekaboo!");
	}

	private EngineResult parse(DummyPlugin plugin, String raw) {
		QContext context = new QContextFactory().make();
		EngineSupplierFactory factory = new DummyBeanFactory().context(context).getEngineSupplierFactory();
		factory.getPluginIndex().init(Arrays.asList(plugin), factory);
		return new EngineService(factory).parse(raw);
	}

	private void invokeOnEngine(QPlugin plugin, QContext context, String raw) {
		EngineSupplierFactory factory = new DummyBeanFactory().context(context).getEngineSupplierFactory();
		factory.getPluginIndex().init(Arrays.asList(plugin), factory);
		new EngineService(factory).invoke(raw);
	}
}
