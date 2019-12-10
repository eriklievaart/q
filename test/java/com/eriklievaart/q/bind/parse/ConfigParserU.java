package com.eriklievaart.q.bind.parse;

import java.util.Arrays;

import org.junit.Test;

import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.schema.IniSchemaException;
import com.eriklievaart.toolkit.mock.BombSquad;

public class ConfigParserU {

	@Test
	public void validateSchemaMenuPass() throws Exception {

		IniNode menuFull = new IniNode("menu", "file");
		menuFull.setProperty("label", "File");
		menuFull.setProperty("mnemonic", "F");
		menuFull.setProperty("order", "0");

		IniNode menuMinimal = new IniNode("menu", "help");
		menuMinimal.setProperty("label", "Help");

		IniNode componentNode = new IniNode("component", "q.left.browser");
		IniNode bindingNode = new IniNode("binding");
		componentNode.addChild(bindingNode);

		bindingNode.setProperty("event", "click3");
		bindingNode.setProperty("action", "q.foo");

		ConfigParser.validateSchema(Arrays.asList(componentNode, menuFull, menuMinimal));
	}

	@Test
	public void validateSchemaMenuFail() throws Exception {
		IniNode menuMinimal = new IniNode("menu", "help");
		menuMinimal.setProperty("fail.validation", "Not in schema");
		BombSquad.diffuse(IniSchemaException.class, "fail.validation", () -> {
			ConfigParser.validateSchema(Arrays.asList(menuMinimal));
		});
	}

	@Test
	public void validateSchemaItemPass() throws Exception {
		IniNode file = new IniNode("menu", "file");
		file.setProperty("label", "File");

		IniNode root = new IniNode("item", "file.root");
		root.setProperty("label", "Jump to Root");
		root.setProperty("action", "q.active.root");
		root.setProperty("mnemonic", "J");
		root.setProperty("accelerator", "ctrl alt /");
		root.setProperty("order", "0");
		file.addChild(root);

		IniNode location = new IniNode("item", "file.location");
		location.setProperty("label", "Open location");
		file.addChild(location);

		ConfigParser.validateSchema(Arrays.asList(file));
	}
}