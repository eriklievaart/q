package com.eriklievaart.q.engine.meta;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.eriklievaart.q.api.QPlugin;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.exception.ShellParseException;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.q.engine.parse.ShellParser;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.MultiMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.reflect.api.GenericsTool;
import com.eriklievaart.toolkit.reflect.api.ReflectException;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotatedMethod;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotationTool;

public class PluginIntrospector {
	private LogTemplate log = new LogTemplate(getClass());

	private final Supplier<Converters> collectionConverters;
	private final Supplier<Converters> basicConverters;

	public PluginIntrospector(EngineSupplierFactory factory) {
		basicConverters = factory.getBasicConvertersSupplier();
		collectionConverters = factory.getCollectionsConvertersSupplier();
	}

	public List<CommandMetadata> generateMetadata(List<QPlugin> plugins) {
		List<CommandMetadata> commands = NewCollection.list();
		for (QPlugin plugin : plugins) {
			CommandMetadata command = getCommandMetadata(plugin);
			commands.add(command);
			log.debug("registering command % => $", command.getCommandName(), command.getFlagString());
		}
		return commands;
	}

	CommandMetadata getCommandMetadata(QPlugin plugin) {
		return new CommandMetadata(plugin, getFlagGroups(plugin));
	}

	private List<FlagGroupMetadata> getFlagGroups(QPlugin plugin) {
		return getFlagGroups(plugin.createInstance().getClass());
	}

	public List<FlagGroupMetadata> getFlagGroups(Class<?> clazz) {
		MultiMap<String, AnnotatedMethod<Flag>> grouped = new MultiMap<>();

		for (AnnotatedMethod<Flag> method : AnnotationTool.getMethodsAnnotatedWith(clazz, Flag.class)) {
			grouped.add(method.getAnnotation().group(), method);
		}
		return getFlagGroups(grouped, clazz);
	}

	private List<FlagGroupMetadata> getFlagGroups(MultiMap<String, AnnotatedMethod<Flag>> groupToMethod, Class<?> clz) {
		List<FlagGroupMetadata> list = NewCollection.list();
		for (String group : groupToMethod.keySet()) {
			list.add(getFlagGroup(group, groupToMethod.get(group)));
		}
		enforceFlagsUnique(list, clz);
		return list;
	}

	private FlagGroupMetadata getFlagGroup(String group, List<AnnotatedMethod<Flag>> methods) {
		List<FlagMetadata> flags = NewCollection.list();
		FlagMetadata primary = null;

		for (AnnotatedMethod<Flag> method : methods) {
			validateFlag(method);
			FlagMetadata flag = new FlagMetadata(method.getName(), loadFlagValues(method));
			flags.add(flag);
			if (method.getAnnotation().primary()) {
				Check.isNull(primary, "Duplicate primary %", flag.getName());
				primary = flag;
			}
		}
		return new FlagGroupMetadata(group, flags, primary);
	}

	static List<ShellArgument> loadFlagValues(final AnnotatedMethod<Flag> annotated) {

		Method member = annotated.getMember();
		String[] values = annotated.getAnnotation().values();
		Class<?>[] types = member.getParameterTypes();
		ReflectException.on(values.length != types.length, "% values required for member %", types.length, member);

		try {
			return parseFlagValues(values);
		} catch (ShellParseException e) {
			throw new ReflectException(member + e.getMessage(), e);
		}
	}

	private static List<ShellArgument> parseFlagValues(final String[] values) throws ShellParseException {
		List<ShellArgument> result = NewCollection.list();

		for (int i = 0; i < values.length; i++) {
			result.add(ShellParser.parseArgument(values[i]));
		}
		return result;
	}

	private void validateFlag(final AnnotatedMethod<Flag> annotated) {
		Converters converters = collectionConverters.get();
		Method member = annotated.getMember();
		Class<?>[] types = member.getParameterTypes();
		Type[] generics = member.getGenericParameterTypes();
		String method = Str.sub("$.$(...)", member.getDeclaringClass().getName(), member.getName());

		for (int i = 0; i < types.length; i++) {
			String msg = "$ unsupported for @Flag $\n$";
			ReflectException.unless(converters.isConvertible(types[i]), msg, types[i], method, converters.getTypes());
			verifyGenerics(annotated, types[i], generics[i]);
		}
	}

	private void verifyGenerics(final AnnotatedMethod<Flag> annotated, final Class<?> literal, final Type generics) {
		Converters converters = basicConverters.get();

		if (literal == List.class) {
			Type type = GenericsTool.getGenericType(generics);
			boolean isConvertible = converters.isConvertible((Class<?>) type);
			ReflectException.unless(isConvertible, "Cannot convert %; only: %", type, converters.getTypes());
		}
		if (literal == Map.class) {
			boolean invalid = !GenericsTool.hasGenericTypes(generics, String.class, String.class);
			ReflectException.on(invalid, "Maps should have Generic types 'String', 'String': %", annotated);
		}
	}

	private static void enforceFlagsUnique(final List<FlagGroupMetadata> list, final Class<?> literal) {
		Set<Character> character = NewCollection.set();

		for (FlagGroupMetadata group : list) {
			for (FlagMetadata item : group.getAllFlags()) {
				boolean duplicate = !character.add(item.getFlag());
				ReflectException.on(duplicate, "Duplicate @Flag % => % on %", item.getFlag(), item.getName(), literal);
			}
		}
	}
}
