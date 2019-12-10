package com.eriklievaart.q.ui.main;

import java.util.function.Consumer;

import javax.swing.JOptionPane;

import com.eriklievaart.toolkit.lang.api.str.Str;

public class Dialogs {

	private UiComponents components;

	public Dialogs(UiComponents components) {
		this.components = components;
	}

	public void input(String message, Consumer<String> consumer) {
		String input = JOptionPane.showInputDialog(message);
		if (Str.notBlank(input)) {
			consumer.accept(input);
		}
	}

	public void input(String message, String initialValue, Consumer<String> consumer) {
		String newName = JOptionPane.showInputDialog(components.mainFrame, message, initialValue);
		if (Str.notBlank(newName)) {
			consumer.accept(newName);
		}
	}

	public void confirm(String message, Runnable runnable) {
		int result = JOptionPane.showConfirmDialog(components.mainFrame, message, null, JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			runnable.run();
		}
	}

	public void message(String message) {
		JOptionPane.showMessageDialog(components.mainFrame, message);
	}
}