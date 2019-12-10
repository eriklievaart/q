package com.eriklievaart.q.bind.parse;

import java.io.InputStream;
import java.util.List;

import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;
import com.eriklievaart.toolkit.io.api.ini.IniNodeValidator;

public class ConfigParser {

	private UiConfig config = new UiConfig();

	public void parse(Long bundleId, InputStream is) {
		if (is != null) {
			List<IniNode> nodes = IniNodeIO.read(is);
			validateSchema(nodes);
			config.addBindings(new BindingParser(bundleId).parse(nodes));
			new MenuParser(config).parse(nodes);
		}
	}

	static void validateSchema(List<IniNode> nodes) {
		List<IniNode> schema = IniNodeIO.read(ConfigParser.class.getResourceAsStream("/bind/schema.txt"));
		IniNodeValidator.validate(nodes, schema.get(0));
	}

	public UiConfig getConfig() {
		return config;
	}
}