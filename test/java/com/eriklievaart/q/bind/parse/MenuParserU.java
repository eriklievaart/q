package com.eriklievaart.q.bind.parse;

import java.util.Arrays;
import java.util.List;

import javax.swing.KeyStroke;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.bind.menu.JMenuItemWrapper;
import com.eriklievaart.q.bind.menu.JMenuWrapper;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;
import com.eriklievaart.toolkit.mock.BombSquad;

public class MenuParserU {

	@Test
	public void parseMenu() {
		IniNode node = new IniNode("menu", "file");
		node.setProperty("label", "File");
		node.setProperty("mnemonic", "F");
		node.setProperty("order", "0");

		UiConfig config = new UiConfig();
		new MenuParser(config).parse(Arrays.asList(node));
		JMenuWrapper wrapper = config.menu.get("file");

		Assertions.assertThat(wrapper.menu.getText()).isEqualTo("File");
		Assertions.assertThat(wrapper.menu.getMnemonic()).isEqualTo('F');
		Assertions.assertThat(wrapper.order).isEqualTo(0);
	}

	@Test
	public void parseMenuMinimal() {
		IniNode node = new IniNode("menu", "help");
		node.setProperty("label", "Help");

		UiConfig config = new UiConfig();
		new MenuParser(config).parse(Arrays.asList(node));
		JMenuWrapper wrapper = config.menu.get("help");

		Assertions.assertThat(wrapper.menu.getText()).isEqualTo("Help");
		Assertions.assertThat(wrapper.order).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	public void parseMenuReference() {
		IniNode original = new IniNode("menu", "file");
		original.setProperty("label", "File");
		original.setProperty("mnemonic", "F");
		original.setProperty("order", "0");

		UiConfig config = new UiConfig();
		new MenuParser(config).parse(Arrays.asList(original));

		IniNode reference = new IniNode("menu", "file");
		new MenuParser(config).parse(Arrays.asList(reference));

		JMenuWrapper wrapper = config.menu.get("file");
		Assertions.assertThat(wrapper.menu.getText()).isEqualTo("File");
		Assertions.assertThat(wrapper.menu.getMnemonic()).isEqualTo('F');
		Assertions.assertThat(wrapper.order).isEqualTo(0);
	}

	@Test
	public void parseMenuItem() {
		IniNode menu = new IniNode("menu", "help");
		menu.setProperty("label", "Help");

		IniNode item = new IniNode("item", "help.about");
		item.setProperty("label", "About");
		item.setProperty("order", "5");
		item.setProperty("action", "help.about.popup");
		item.setProperty("mnemonic", "A");
		item.setProperty("accelerator", "F1");
		menu.addChild(item);

		UiConfig config = new UiConfig();
		new MenuParser(config).parse(Arrays.asList(menu));
		JMenuWrapper wrapper = config.menu.get("help");

		Assertions.assertThat(wrapper.menu.getText()).isEqualTo("Help");
		Assertions.assertThat(wrapper.order).isEqualTo(Integer.MAX_VALUE);

		List<JMenuItemWrapper> children = wrapper.getItems();
		JMenuItemWrapper unwrapped = CollectionTool.getSingle(children);
		Assertions.assertThat(unwrapped.order).isEqualTo(5);
		Assertions.assertThat(unwrapped.action).isEqualTo("help.about.popup");
		Assertions.assertThat(unwrapped.item.getText()).isEqualTo("About");
		Assertions.assertThat(unwrapped.item.getMnemonic()).isEqualTo('A');
		Assertions.assertThat(unwrapped.item.getAccelerator()).isEqualTo(KeyStroke.getKeyStroke("F1"));
	}

	@Test
	public void parseMenuItemInvalidAccelerator() {
		IniNode menu = new IniNode("menu", "help");
		menu.setProperty("label", "Help");

		IniNode item = new IniNode("item", "help.about");
		item.setProperty("label", "About");
		item.setProperty("action", "help.about.popup");
		item.setProperty("accelerator", "esc");
		menu.addChild(item);

		UiConfig config = new UiConfig();
		BombSquad.diffuse(AssertionException.class, "Invalid KeyStroke `esc`", () -> {
			new MenuParser(config).parse(Arrays.asList(menu));
		});
	}
}