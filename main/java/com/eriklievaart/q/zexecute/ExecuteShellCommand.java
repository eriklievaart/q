package com.eriklievaart.q.zexecute;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.runtime.api.CliCommand;
import com.eriklievaart.toolkit.runtime.api.CliInvoker;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("execute files in system specific browsers, editors, etc.")
class ExecuteShellCommand implements Invokable {
	private LogTemplate log = new LogTemplate(getClass());
	private ExecuteController controller;

	private Mode mode = Mode.COMMAND;
	private File file;
	private URI uri;
	private boolean shell = true;
	private boolean panel = false;
	private List<VirtualFile> files = Collections.emptyList();

	private enum Mode {
		BROWSE, COMMAND, EACH, INSIDE, OPEN, TERMINAL
	}

	public ExecuteShellCommand(ExecuteController components) {
		this.controller = components;
	}

	@Flag(group = "main", values = "$dir")
	@Doc("Open a URI in the system browser.")
	public ExecuteShellCommand browse(final URI browse) {
		mode = Mode.BROWSE;
		this.uri = browse;
		return this;
	}

	@Flag(group = "main", values = "$dir", primary = true)
	@Doc("Execute a command on the native command line.")
	public ExecuteShellCommand command(final File dir) {
		mode = Mode.COMMAND;
		this.file = dir;
		return this;
	}

	@Flag(group = "main", values = "$url")
	@Doc("Open a File with the system default application.")
	public ExecuteShellCommand open(final File open) {
		mode = Mode.OPEN;
		this.file = open;
		return this;
	}

	@Flag(group = "main", values = { "$dir", "$urls" })
	@Doc("Run shell command for each file.")
	public ExecuteShellCommand each(File dir, List<VirtualFile> urls) {
		mode = Mode.EACH;
		this.file = dir;
		this.files = urls;
		return this;
	}

	@Flag(group = "main", values = { "$urls" })
	@Doc("Run shell command in each selected directory.")
	public ExecuteShellCommand inside(List<VirtualFile> urls) {
		mode = Mode.INSIDE;
		this.files = urls;
		return this;
	}

	@Flag(group = "main", values = "$dir")
	@Doc("Open a terminal window.")
	public ExecuteShellCommand terminal(final File dir) {
		mode = Mode.TERMINAL;
		this.file = dir;
		return this;
	}

	@Flag(group = "shell")
	@Doc("Do not invoke native shell('cmd /c' or 'sh -c'), but use spaces to separate flags.")
	public ExecuteShellCommand spaces() {
		shell = false;
		return this;
	}

	@Flag(group = "output")
	@Doc("Show the resulting output.")
	public ExecuteShellCommand frame() {
		panel = true;
		return this;
	}

	@Override
	@Doc("Command to invoke on the native command line.")
	public void invoke(PluginContext context) throws Exception {
		invokeMode(context);
	}

	@Doc("pipe command to execute on native command line (for applicable flags)")
	private void invokeMode(PluginContext context) throws IOException {
		switch (mode) {

		case BROWSE:
			Desktop.getDesktop().browse(uri);
			return;

		case COMMAND:
			executeCommand(context.getPipedContents(), file, null);
			return;

		case EACH:
			runOnEachFile(context);
			return;

		case INSIDE:
			runInEachDirectory(context);
			return;

		case OPEN:
			Desktop.getDesktop().open(file);
			return;

		case TERMINAL:
			executeCommand(getTerminalCommand(), file, null);
			return;

		default:
			throw new IllegalStateException("Unknown enum type: " + mode);
		}
	}

	private void runOnEachFile(PluginContext context) {
		for (VirtualFile each : files) {
			executeCommand(context.getPipedContents(), file, each);
		}
	}

	private void runInEachDirectory(PluginContext context) {
		for (VirtualFile each : files) {
			SystemFile sf = (SystemFile) each;
			executeCommand(context.getPipedContents(), sf.unwrap(), each);
		}
	}

	private String getTerminalCommand() {
		return isWindows() ? "cmd" : "xterm";
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	private void executeCommand(final String raw, File directory, VirtualFile url) {
		String substituted = url == null ? raw : substitute(raw, url);
		CliCommand cmd = shell ? nativeShellCommand(substituted) : CliCommand.from(substituted);

		log.trace("Should show terminal output: $", panel);
		if (panel) {
			controller.showPanel();
			controller.printHeader(raw.trim());
			CliInvoker.invoke(cmd, directory, controller.createOutputStreams());

		} else {
			CliInvoker.invoke(cmd, directory);
		}
	}

	static String substitute(String raw, VirtualFile url) {
		String replaced = raw;
		replaced = replaced.replace("$url", url.getUrl().getUrlUnescaped());
		replaced = replaced.replace("$!url", url.getUrl().getUrlEscaped());
		replaced = replaced.replace("$path", url.getPath());
		replaced = replaced.replace("$!path", url.getUrl().getPathEscaped());
		replaced = replaced.replace("$name", url.getName());
		replaced = replaced.replace("$!name", url.getUrl().getNameEscaped());
		replaced = replaced.replace("$base", url.getBaseName());
		replaced = replaced.replace("$!base", url.getUrl().getBaseNameEscaped());
		replaced = replaced.replace("$ext", url.getExtension());
		replaced = replaced.replace("$!ext", UrlTool.escape(url.getExtension()));
		return replaced;
	}

	CliCommand nativeShellCommand(final String raw) {
		String command = isWindows() ? "cmd" : "sh";
		String flag = isWindows() ? "/c" : "-c";
		return new CliCommand(command, flag, raw);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		if (mode == Mode.BROWSE) {
			PluginException.on(uri == null, "Not a uri: %", uri);
		} else if (mode == Mode.OPEN) {
			PluginException.on(file == null || !file.exists(), "% does not exist", file);
		} else if (mode == Mode.INSIDE) {
			for (VirtualFile dir : files) {
				PluginException.on(dir == null || !dir.isDirectory(), "% is not a directory", dir);
			}
		} else {
			PluginException.on(file == null || !file.isDirectory() || !file.exists(), "Not a directory: %", file);
		}
		if (Str.notBlank(context.getPipedContents())) {
			CliCommand.from(context.getPipedContents());
		}
	}
}