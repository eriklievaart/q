package com.eriklievaart.q.engine.meta;

import org.junit.Test;

import com.eriklievaart.q.api.engine.CallPolicy;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.engine.parse.ShellCommandFactory;
import com.eriklievaart.toolkit.lang.api.AssertionException;

public class MetadataValidatorU {
	private static final boolean ERROR = true;

	@Test
	public void validateCommand() {
		test(new ShellCommandFactory("delete"), new CommandMetadataFactory("delete").make());
	}

	@Test
	public void validateShortCommand() {
		test(new ShellCommandFactory("d"), new CommandMetadataFactory("delete").make());
	}

	@Test
	public void validateWrongCommand() {
		test(new ShellCommandFactory("copy", ERROR), new CommandMetadataFactory("delete").make());
	}

	@Test
	public void validateRunnableSuccess() {
		test(new ShellCommandFactory("run"), new CommandMetadataFactory("run").make());
	}

	@Test
	public void validateRunnableFail() {
		CommandMetadata metadata = new CommandMetadataFactory("swallow", CallPolicy.FLAGS_ONLY).make();
		test(new ShellCommandFactory("swallow", ERROR).swallow("ant"), metadata);
	}

	@Test
	public void validateSwallowSuccess() {
		CommandMetadata metadata = new CommandMetadataFactory("swallow", CallPolicy.PIPED).make();
		test(new ShellCommandFactory("swallow").swallow("sword"), metadata);
	}

	@Test
	public void validateSwallowFail() {
		CommandMetadata metadata = new CommandMetadataFactory("run", CallPolicy.PIPED).make();
		test(new ShellCommandFactory("run", ERROR), metadata);
	}

	@Test
	public void validateSingleFlag() {
		test(new ShellCommandFactory("run").flags('f'), new CommandMetadataFactory("run").mutexFlag("flag").make());
	}

	@Test
	public void validateDoubleFlag() {
		CommandMetadata metadata = new CommandMetadataFactory("run").flag("alpha").flag("beta").make();
		test(new ShellCommandFactory("run").flags('a', 'b'), metadata);
	}

	@Test
	public void validateMissingFlag() {
		CommandMetadata metadata = new CommandMetadataFactory("run").mutexFlag("alpha").mutexFlag("beta").make();
		test(new ShellCommandFactory("run", ERROR).flags('c'), metadata);
	}

	@Test
	public void validateFlagGroupDistinct() {
		CommandMetadata metadata = new CommandMetadataFactory("run").flag("single").flag("wildcard").make();
		test(new ShellCommandFactory("run").flags('s', 'w'), metadata);
	}

	@Test
	public void validateFlagGroupDuplicate() {
		CommandMetadata metadata = new CommandMetadataFactory("run").mutexFlag("single").mutexFlag("multiple").make();
		test(new ShellCommandFactory("run", ERROR).flags('s', 'm'), metadata);
	}

	private static void test(final ShellCommandFactory builder, final CommandMetadata meta) {
		try {
			MetadataValidator.validate(builder.make(), meta);
			if (builder.expectError()) {
				throw new AssertionException("Error expected");
			}

		} catch (PluginException ve) {
			if (!builder.expectError()) {
				throw new AssertionException("Unexpected error: " + ve.getMessage());
			}
		}
	}
}