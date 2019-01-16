package com.eriklievaart.q.engine.meta;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.reflect.api.ReflectException;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotatedMethod;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotationTool;
import com.eriklievaart.toolkit.test.api.BombSquad;

@SuppressWarnings("unused")
public class PluginIntrospectorU {
	private final List<ShellArgument> NO_ARGS = Collections.emptyList();
	private final boolean COLLECTION = true;
	private final boolean SINGLE = false;
	private PluginIntrospector introspector;

	@Before
	public void init() {
		introspector = new PluginIntrospector(new DummyBeanFactory().getEngineSupplierFactory());
	}

	// ///////////////
	// //// Flags ////
	// ///////////////

	@Test
	public void loadFlagString() throws PluginException {
		class Local {
			@Flag(values = "``")
			public void flag(final String flag) {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").addArgument("``").make());
	}

	@Test
	public void loadFlagDuplicate() throws PluginException {
		@Doc("dummy doc")
		class Local implements Invokable {
			@Flag
			@Doc("dummy doc")
			public void flag() {
			}

			@Flag
			@Doc("dummy doc")
			public void flagDuplicate() {
			}

			@Override
			public void invoke(PluginContext context) throws Exception {
			}

			@Override
			public void validate(PluginContext context) throws PluginException {
			}
		}
		BombSquad.diffuseRegex(ReflectException.class, "Duplicate.*@Flag", () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagBoolean() throws PluginException {
		class Local {
			@Flag
			public void flag() {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").make());
	}

	@Test
	public void loadFlagInteger() throws PluginException {
		class Local {
			@Flag
			public void flag(final Integer unsupported) {
			}
		}
		BombSquad.diffuse(ReflectException.class, Arrays.asList("unsupported", "@Flag"), () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagListWithArguments() throws PluginException {
		class Local {
			@Flag(values = "``")
			public void flag(final List<String> arguments) {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").addArgument("``").make());
	}

	@Test
	public void loadFlagListOfFiles() throws PluginException {
		class Local {
			@Flag(values = "``")
			public void flag(final List<File> supported) {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").addArgument("``").make());
	}

	@Test
	public void loadFlagListWrongType() throws PluginException {
		class Local {
			@Flag
			public void flag(final List<Integer> unsupported) {
			}
		}
		BombSquad.diffuse(ReflectException.class, "Cannot convert `class java.lang.Integer`", () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagMapWithArguments() throws PluginException {
		class Local {
			@Flag(values = "`key=value`")
			public void flag(final Map<String, String> arguments) {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").addArgument("`key=value`").make());
	}

	@Test
	public void loadFlagMapWrongTypeValue() throws PluginException {
		class Local {
			@Flag
			public void flag(final Map<String, Integer> unsupported) {
			}
		}
		BombSquad.diffuse(ReflectException.class, "Maps should have Generic type", () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagMapWrongTypeKey() throws PluginException {
		class Local {
			@Flag
			public void flag(final Map<Integer, String> unsupported) {
			}
		}
		BombSquad.diffuse(ReflectException.class, "Maps should have Generic type", () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagUnsupportedType() throws PluginException {
		class Local {
			@Flag
			public void flag(final Thread unsupported) {
			}
		}
		BombSquad.diffuseRegex(ReflectException.class, "Unsupported.*@Flag.*", () -> {
			introspect(Local.class);
		});
	}

	@Test
	public void loadFlagValueEmpty() throws PluginException {
		class Local {
			@Flag(values = {})
			public void flag() {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").make());
	}

	@Test
	public void loadFlagWithGroup() throws PluginException {
		class Local {
			@Flag(group = "group")
			public void flag() {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").make());
	}

	@Test
	public void loadFlagValue() throws PluginException {
		class Local {
			@Flag(values = { "`value`" })
			public void flag(final String argument) {
			}
		}
		Check.isEqual(loadFlag(Local.class), FlagMetadataFactory.named("flag").addArgument("`value`").make());
	}

	@Test
	public void loadFlagValues() throws PluginException {
		class Local {
			@Flag(values = { "`one two`" })
			public void flag(final List<String> arguments) {
			}
		}
		FlagMetadata expected = FlagMetadataFactory.named("flag").addArgument("`one two`").make();
		Check.isEqual(loadFlag(Local.class), expected);
	}

	@Test(expected = ReflectException.class)
	public void loadFlagTooManyValues() throws PluginException {
		class Local {
			@Flag(values = { "one", "two" })
			public void flag(final String argument) {
			}
		}
		loadFlagValues(Local.class);
	}

	@Test(expected = ReflectException.class)
	public void loadFlagTooFewValues() throws PluginException {
		class Local {
			@Flag(values = { "one" })
			public void flag(final List<String> arguments) {
			}
		}
		loadFlagValues(Local.class);
	}

	// ///////////////
	// //// Utils ////
	// ///////////////

	private List<FlagGroupMetadata> introspect(Class<?> type) {
		return introspector.getFlagGroups(type);
	}

	private List<FlagMetadata> flag(final String name, final String group, final List<ShellArgument> values,
			final boolean mutivalued) {
		return Arrays.asList(new FlagMetadata(name, values));
	}

	private List<ShellArgument> loadFlagValues(final Class<?> clazz) {
		AnnotatedMethod<Flag> member = AnnotationTool.getMethodsAnnotatedWith(clazz, Flag.class).get(0);
		return PluginIntrospector.loadFlagValues(member);
	}

	private FlagMetadata loadFlag(final Class<?> literal) {
		return introspector.getFlagGroups(literal).get(0).getAllFlags().get(0);
	}
}
