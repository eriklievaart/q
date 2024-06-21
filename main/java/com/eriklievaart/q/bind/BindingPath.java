package com.eriklievaart.q.bind;

import java.io.File;

public class BindingPath {
	private File actions = new File(System.getProperty("user.home") + "/.config/q/actions.properties");
	private File bindings = new File(System.getProperty("user.home") + "/.config/q/bindings.ini");

	public File getActionFile() {
		return actions;
	}

	public File getBindingFile() {
		return bindings;
	}
}
