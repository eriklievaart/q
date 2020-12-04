package com.eriklievaart.q.ui.api;

import java.util.function.Consumer;

public interface Dialogs {

	public void input(String message, Consumer<String> consumer);

	public void input(String message, String initialValue, Consumer<String> consumer);

	public void confirm(String message, Runnable runnable);

	public void message(String message);

	public void choice(String message, String[] options, Consumer<String> consumer);
}