package com.eriklievaart.q.zexecute;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.runtime.api.CliCommand;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFile;
import com.eriklievaart.toolkit.vfs.api.file.MemoryFileSystem;

public class ExecuteShellCommandU {

	@Test
	public void substitute() throws Exception {
		MemoryFileSystem memory = new MemoryFileSystem();
		MemoryFile file = memory.resolve("ram/file name.txt");

		Check.isEqual(ExecuteShellCommand.substitute("$url", file), "mem:///ram/file name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$!url", file), "mem:///ram/file%20name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$path", file), "/ram/file name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$!path", file), "/ram/file%20name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$name", file), "file name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$!name", file), "file%20name.txt");
		Check.isEqual(ExecuteShellCommand.substitute("$base", file), "file name");
		Check.isEqual(ExecuteShellCommand.substitute("$!base", file), "file%20name");
		Check.isEqual(ExecuteShellCommand.substitute("$ext", file), "txt");
	}

	@Test
	public void nativeShellCommand() {
		CliCommand command = new ExecuteShellCommand(null).nativeShellCommand("ls \\`");
		String[] split = command.cmd();

		Check.isEqual(split[0], "sh");
		Check.isEqual(split[1], "-c");
		Check.isEqual(split[2], "ls \\`"); // real command as single argument
	}
}
