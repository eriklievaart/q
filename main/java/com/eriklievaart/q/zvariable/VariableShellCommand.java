package com.eriklievaart.q.zvariable;

import java.util.function.Consumer;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.str.CharIterator;
import com.eriklievaart.toolkit.lang.api.str.StringBuilderWrapper;

@Doc("replace @{} substitution variables")
public class VariableShellCommand implements Invokable {

	private Consumer<String> consumer;

	public VariableShellCommand(Consumer<String> consumer) {
		this.consumer = consumer;
	}

	@Override
	@Doc("Command to substitute")
	public void invoke(PluginContext context) throws Exception {
		String piped = context.getPipedContents();
		CharIterator iterator = new CharIterator(piped);
		consumer.accept(substitute(iterator, context));
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
	}

	private String substitute(CharIterator iterator, PluginContext context) throws PluginException {
		StringBuilderWrapper builder = new StringBuilderWrapper();

		while (iterator.hasNext()) {
			iterator.find("@{", builder);
			if (!iterator.hasNext()) {
				break;
			}
			iterator.skip(2);
			StringBuilderWrapper variable = new StringBuilderWrapper();
			iterator.find("}", variable);
			if (!iterator.hasNext()) {
				throw new PluginException("expecting '}' => " + variable);
			}
			iterator.skip();
			builder.append(lookup(variable.toString(), context));
		}
		return builder.toString();
	}

	private String lookup(String query, PluginContext context) throws PluginException {
		String[] split = query.split(":");
		String variable = split[0].replaceAll("\\W", "");
		String attribute = split.length == 1 ? "url" : split[1].replaceAll("\\W", "");

		String[] values = context.getVariable(variable).split("\\s+");
		for (int i = 0; i < values.length; i++) {
			values[i] = selectUrlPart(values[i], attribute);
			if (!query.contains("/")) {
				values[i] = UrlTool.unescape(values[i]);
			}
			if (query.contains("'")) {
				values[i] = "'" + values[i].replaceAll("'", "\\\\'") + "'";
			}
		}
		return String.join(" ", values);
	}

	private String selectUrlPart(String url, String attribute) throws PluginException {
		switch (attribute) {

		case "url":
			return url;

		case "path":
			return UrlTool.getPath(url);

		case "name":
			return UrlTool.getName(url);

		case "base":
			return UrlTool.getBaseName(url);

		case "ext":
			return UrlTool.getExtension(url);
		}
		throw new PluginException("Invalid attribute selector ':$'", attribute);
	}
}
