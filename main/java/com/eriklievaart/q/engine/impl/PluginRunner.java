package com.eriklievaart.q.engine.impl;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.engine.PluginIndex;
import com.eriklievaart.q.engine.exception.ShellException;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.meta.FlagMetadata;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.toolkit.convert.api.ConversionException;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.reflect.api.GenericsTool;
import com.eriklievaart.toolkit.reflect.api.InstanceTool;
import com.eriklievaart.toolkit.reflect.api.method.MethodWrapper;

public class PluginRunner {
	private LogTemplate log = new LogTemplate(getClass());

	private PluginIndex index;
	private Supplier<Converters> converters;

	public PluginRunner(PluginIndex index, Supplier<Converters> converters) {
		this.index = index;
		this.converters = converters;
	}

	public void run(ShellCommand command, PluginContext context) throws Exception {
		init(command, context).invoke(context);
	}

	public void validate(ShellCommand command, PluginContext context) throws Exception {
		init(command, context).validate(context);
	}

	private Invokable init(ShellCommand command, PluginContext context) throws ShellException {
		Invokable invokable = index.lookup(command.getName()).get().getPlugin().createInstance();
		callFlags(invokable, loadFlags(command, context));
		return invokable;
	}

	Map<String, String[]> loadFlags(final ShellCommand command, final PluginContext context) throws ShellException {
		CommandMetadata info = index.lookup(command.getName()).get();

		Iterator<ShellArgument> iter = command.getArguments();
		Map<String, String[]> flagArguments = NewCollection.map();

		for (char c : info.addDefaultFlags(command.getFlags())) {
			FlagMetadata flag = info.getFlagMetadata(c);
			flagArguments.put(flag.getName(), extractArguments(flag.getShellArguments(iter), context));
		}
		if (iter.hasNext()) {
			throw new ShellException("too many flags!");
		}
		return flagArguments;
	}

	String[] extractArguments(final ShellArgument[] arguments, final PluginContext context) {

		String[] parameters = new String[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			parameters[i] = resolve(arguments[i], context);
		}
		return parameters;
	}

	String resolve(final ShellArgument argument, final PluginContext context) {
		switch (argument.getType()) {

		case VARIABLE:
			String variable = context.getVariable(argument.getValue());
			Check.notNull(variable, "Variable not found: %", argument.getValue());
			return variable;

		case STRING:
			return argument.getValue();
		}
		throw new IllegalArgumentException(Str.sub("Unknown enum constant: %", argument.getType()));
	}

	void callFlags(final Object instance, final Map<String, String[]> flags) {
		for (String method : flags.keySet()) {
			MethodWrapper wrapper = InstanceTool.getMethodWrapper(method, instance);
			wrapper.invoke(convertFlags(flags.get(method), wrapper.getGenericArgumentTypes()));
		}
	}

	private Object[] convertFlags(final Object[] args, final Type[] types) {
		Check.notNull(args);

		try {
			Object[] out = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				out[i] = convertValue(types[i], args[i].toString());
				log.trace("converted % -> %", args[i], out[i]);
			}
			return out;
		} catch (ConversionException e) {
			throw new FormattedException("%; could not convert argument", e, e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	Object convertValue(final Type toType, final String value) throws ConversionException {
		Converters convert = converters.get();
		if (!GenericsTool.isBaseType(toType, List.class)) {
			return convert.to(GenericsTool.getLiteral(toType), UrlTool.unescape(value));
		}
		List<String> strings = convert.to(List.class, value);
		Class<?> genericType = (Class<?>) GenericsTool.getGenericType(toType);

		List converted = NewCollection.list();
		for (String raw : strings) {
			converted.add(convert.to(genericType, UrlTool.unescape(raw)));
		}
		return converted;
	}
}