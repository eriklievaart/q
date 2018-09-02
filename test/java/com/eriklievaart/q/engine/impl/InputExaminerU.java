package com.eriklievaart.q.engine.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.engine.meta.CommandMetadataFactory;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;

public class InputExaminerU {

	@Test
	public void examineParseException() {
		Check.isTrue(examineRaw(new CommandMetadataFactory("dummy"), "?").isError());
	}

	@Test
	public void examineMissingCommand() {
		EngineResult result = examineRaw(new CommandMetadataFactory("dummy"), "idonotexist");

		Check.isTrue(StringUtils.containsIgnoreCase(result.getMessage(), "unrecognized"), result.getMessage());
		Check.isTrue(result.isError());
	}

	@Test
	public void examineCommand() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new"), "n");

		Check.isEqual(result.getMessage(), "new");
		Check.isFalse(result.isError());
	}

	@Test
	public void missingCommand() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new"), "-");

		Check.isTrue(result.isError());
	}

	@Test
	public void missingCommandFlag() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new"), "aap -");

		Check.isTrue(result.getMessage().contains("aap"), "Expected 'aap' in message: %", result.getMessage());
	}

	@Test
	public void missingFlag() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new").flag("test"), "n -");

		Check.isTrue(result.getMessage().contains("-[t]"), "Expected flag 't' in message: %", result.getMessage());
	}

	@Test
	public void examineFlagLiteralArgument() {
		EngineResult r = examineRaw(new CommandMetadataFactory("new").mutexFlag("file", "``"), "n-f`arg`");

		Check.isEqual(r.getMessage(), "new -file `arg`");
		Check.isFalse(r.isError());
	}

	@Test
	public void examineFlagLiteralEscaped() {
		EngineResult r = examineRaw(new CommandMetadataFactory("new").mutexFlag("file", "``"), "n-f`\\b`");

		Check.isEqual(r.getMessage(), "new -file `\\`");
		Check.isFalse(r.isError());
	}

	@Test
	public void examineFlagVariableArgument() {
		EngineResult r = examineRaw(new CommandMetadataFactory("open").mutexFlag("file", "$dir"), "o -f $url");

		CheckStr.containsIgnoreCase(r.getMessage(), "open -file $url");
		Check.isFalse(r.isError());
	}

	@Test
	public void examineFlagOptionalLiteral() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new").mutexFlag("file", "`file`"), "n-f");

		Check.isEqual(result.getMessage(), "new -file `file`");
		Check.isFalse(result.isError());
	}

	@Test
	public void examineFlagOptionalVariable() {
		EngineResult result = examineRaw(new CommandMetadataFactory("new").mutexFlag("file", "$dir"), "n-f");

		Check.isEqual(result.getMessage(), "new -file $dir");
		Check.isFalse(result.isError());
	}

	@Test
	public void examineMultipleFlags() {
		CommandMetadataFactory builder = new CommandMetadataFactory("move").flag("file").flag("wildcard");
		EngineResult result = examineRaw(builder, "m-fw$dir`*.bat`");

		Check.isEqual(result.getMessage(), "move -file $dir -wildcard `*.bat`");
		Check.isFalse(result.isError());
	}

	@Test
	public void examineSwallowed() {
		EngineResult result = examineRaw(new CommandMetadataFactory("regex"), "regex|[0-9]++");

		Check.isEqual(result.getMessage(), "regex|[0-9]++");
		Check.isFalse(result.isError());
	}

	private static EngineResult examineRaw(CommandMetadataFactory metadata, String raw) {
		PluginIndex index = metadata.makeIndex();
		EngineSupplierFactory factory = new DummyBeanFactory().pluginIndex(index).getEngineSupplierFactory();
		return factory.getInputExaminer().examineRaw(raw);
	}
}
