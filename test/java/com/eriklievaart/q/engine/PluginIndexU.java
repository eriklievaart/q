package com.eriklievaart.q.engine;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class PluginIndexU {

	@Test
	public void generateMinimal() {
		PluginIndex index = new PluginIndex();
		DummyPlugin plugin = new DummyPlugin("minimal", new EmptyInvokable());

		init(index, plugin);
		Assertions.assertThat(index.listCommands()).containsExactly("minimal");
		Assertions.assertThat(index.lookup("min").get().getCommandName()).isEqualTo("minimal");
		Check.isTrue(index.lookup("ma").isEmpty());
	}

	@Test
	public void generateWithFlag() {
		PluginIndex index = new PluginIndex();

		DummyPlugin plugin = new DummyPlugin("command", new EmptyInvokable() {
			@Flag(group = "main", primary = true)
			public void flag() {
			}
		});

		init(index, plugin);
		CommandMetadata command = index.lookup("command").get();
		Assertions.assertThat(command.getCharacterFlags()).containsExactly('f');
		Check.isEqual(command.getGroup('f'), "main");
	}

	private void init(PluginIndex index, QPlugin plugin) {
		index.init(Arrays.asList(plugin), new DummyBeanFactory().getEngineSupplierFactory());
	}
}
