package com.eriklievaart.q.doc.generate;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.engine.DummyPlugin;
import com.eriklievaart.q.engine.impl.ShellStringBuilder;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.meta.FlagMetadata;
import com.eriklievaart.q.engine.meta.PluginIntrospector;
import com.eriklievaart.q.engine.osgi.DummyBeanFactory;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.engine.parse.ShellArgument;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.ResourceTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.reflect.api.MethodTool;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotatedMethod;
import com.eriklievaart.toolkit.reflect.api.annotations.AnnotationTool;

public class DocGenerator {
	private final LogTemplate log = new LogTemplate(getClass());

	private FreemarkerGenerator freemarker = new FreemarkerGenerator();
	private EngineSupplierFactory factory = new DummyBeanFactory().getEngineSupplierFactory();
	private File sources;
	private File output;

	public void invoke() {
		Map<String, Class<?>> commands = new ShellCommandScanner().scan(sources);
		List<String> plugins = ListTool.sortedCopy(commands.keySet());

		generateManualPage("introduction", plugins);
		generateManualPage("syntax", plugins);
		generateManualPage("templates", plugins);
		generateManualPage("commands", plugins);

		commands.forEach((name, literal) -> {
			generateCommandPage(name, literal, plugins);
		});
	}

	private void generateManualPage(String page, List<String> plugins) {
		String html = freemarker.generateManualHtml(page, plugins);
		store(html, outputFile(page));
	}

	private void generateCommandPage(String name, Class<?> literal, List<String> plugins) {
		CommandContext context = new CommandContext(name);
		context.doc = getClassDoc(literal);
		context.metadata = getMetadata(name, literal);
		context.flagDoc = mapFlagDoc(literal);
		context.examples = mapExamples(context.metadata);
		context.flagless = flagless(context.metadata);
		context.piped = getPiped(literal);
		context.plugins = plugins;
		context.description = loadDescription(name);

		String html = freemarker.generateCommandHtml(context);
		store(html, outputFile(name));
	}

	private void store(String html, File file) {
		log.info("Writing HTML to file %", file);
		FileTool.writeStringToFile(html, file);
	}

	private String loadDescription(String name) {
		Optional<File> optional = ResourceTool.getOptionalFile("/freemarker/description/" + name + ".txt");
		return optional.isPresent() ? FileTool.toString(optional.get()) : null;
	}

	private File outputFile(String name) {
		return new File(output, name + ".xhtml");
	}

	private String getPiped(Class<?> literal) {
		Method method = MethodTool.getMethod(literal, "invoke");
		Doc doc = method.getAnnotation(Doc.class);
		return doc == null ? null : doc.value();
	}

	private String flagless(CommandMetadata metadata) {
		return getExample(metadata, metadata.addDefaultFlags(new char[] {}));
	}

	private Map<String, String> mapExamples(CommandMetadata metadata) {
		Map<String, String> map = new Hashtable<>();

		for (char flag : metadata.getCharacterFlags()) {
			String flagName = metadata.getFlagMetadata(flag).getName();
			char[] flags = metadata.addDefaultFlags(new char[] { flag });
			String example = getExample(metadata, flags);
			map.put(flagName, example);
		}
		return map;
	}

	private String getExample(CommandMetadata metadata, char[] flags) {
		ShellStringBuilder builder = new ShellStringBuilder(metadata.getCommandName());
		for (char flag : flags) {
			builder.appendFlagName("" + flag);
			FlagMetadata flagMetadata = metadata.getFlagMetadata(flag);
			for (ShellArgument argument : flagMetadata.getShellArguments(Collections.emptyIterator())) {
				builder.appendArgument(argument);
			}
		}
		return builder.toString();
	}

	private Map<String, String> mapFlagDoc(Class<?> literal) {
		Map<String, String> map = new Hashtable<>();
		for (AnnotatedMethod<Flag> flags : AnnotationTool.getMethodsAnnotatedWith(literal, Flag.class)) {
			Doc doc = flags.getMember().getAnnotation(Doc.class);
			Check.notNull(doc, "missing @Doc on $", flags.getMember());
			map.put(flags.getName(), doc.value());
		}
		return map;
	}

	private CommandMetadata getMetadata(String name, Class<?> literal) {
		DummyPlugin dummy = new DummyPlugin(name, null);
		return new CommandMetadata(dummy, new PluginIntrospector(factory).getFlagGroups(literal));
	}

	private String getClassDoc(Class<?> literal) {
		Doc annotation = AnnotationTool.getLiteralAnnotation(literal, Doc.class);
		Check.notNull(annotation, "Missing annotation on $", literal);
		return annotation.value();
	}

	public void setSourceDir(File sources) {
		this.sources = sources;
	}

	public void setOutputDir(File output) {
		this.output = output;
	}

}
