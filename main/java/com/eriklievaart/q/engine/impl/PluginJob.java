package com.eriklievaart.q.engine.impl;

import java.util.Optional;

import javax.swing.JOptionPane;

import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.lang.api.ThrowableTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * An excutable version of a ShellCommand. Calling the run method will execute the ShellCommand immediately
 *
 * @author Erik Lievaart
 */
public class PluginJob implements Runnable {

	private final long id;
	private boolean error = false;
	private Optional<String> errorMessage = Optional.empty();

	private ShellCommand command;
	private PluginContext context;
	private PluginRunner runner;
	private String label;

	public PluginJob(long id, ShellCommand command, PluginContext context) {
		Check.notNull(command, context);
		this.id = id;
		this.command = command;
		this.context = context;
	}

	public void setPluginRunner(PluginRunner pr) {
		runner = pr;
	}

	/**
	 * Invoke the ShellCommand.
	 */
	@Override
	public void run() {
		try {
			runner.run(command, context);

		} catch (Exception e) {
			e.printStackTrace();
			if (isTestMode()) {
				throw new RuntimeException(e);
			}
			error = true;
			errorMessage = Optional.of(e.getMessage() == null ? "null" : e.getMessage());

			String trace = ThrowableTool.toString(e);
			String simple = e.getClass().getSimpleName();
			String message = Str.sub("$\n\n$", label, trace);
			JOptionPane.showMessageDialog(null, message, simple, JOptionPane.WARNING_MESSAGE);
		}
	}

	private boolean isTestMode() {
		String property = System.getProperty("application.test.mode");
		return property == null ? false : property.equalsIgnoreCase("true");
	}

	/**
	 * Get the display label.
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String value) {
		label = value;
	}

	/**
	 * Get the error flag.
	 *
	 * @return true if the error flag was set. Until the run method is invoked, this method always returns false.
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Message of the error that occurred when running this job.
	 */
	public Optional<String> getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Get the name of the plug in to invoke.
	 */
	public String getCommandName() {
		return command.getName();
	}

	/**
	 * Unique identifier for the job.
	 */
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
