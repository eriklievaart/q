package com.eriklievaart.q.doc.generate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

public class FreemarkerGenerator {

	Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

	public FreemarkerGenerator() {
		TemplateLoader tl = new ClassTemplateLoader(getClass(), "/freemarker");
		cfg.setTemplateLoader(tl);
	}

	public String generateCommandHtml(CommandContext context) {
		Map<String, Object> data = new HashMap<>();

		data.put("command", context.command);
		data.put("doc", context.doc);
		data.put("metadata", context.metadata);
		data.put("flagDoc", context.flagDoc);
		data.put("examples", context.examples);
		data.put("default", context.flagless);
		data.put("piped", context.piped);
		data.put("plugins", context.plugins);
		data.put("description", context.description);

		return process("plugin.tpl", data);
	}

	public String generateManualHtml(String page, List<String> plugins) {
		return process(page + ".tpl", MapTool.of("plugins", plugins));
	}

	private String process(String template, Map<String, Object> data) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(baos);
			cfg.getTemplate(template).process(data, writer);
			writer.flush();
			return baos.toString("UTF-8");

		} catch (Exception e) {
			throw new RuntimeIOException(e);
		}
	}
}
