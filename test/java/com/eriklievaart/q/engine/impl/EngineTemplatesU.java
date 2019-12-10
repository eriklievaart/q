package com.eriklievaart.q.engine.impl;

import java.util.Map;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.mock.BombSquad;

public class EngineTemplatesU {

	@Test
	public void applyDefault() {
		EngineTemplates templates = new EngineTemplates();
		templates.init(MapTool.of("", "${}", "!", "execute -c | ${}"));
		Check.isEqual(templates.apply("a"), "a");
	}

	@Test
	public void applyPrefix() {
		EngineTemplates templates = new EngineTemplates();
		templates.init(MapTool.of("", "${}", "!", "execute -c | ${}"));
		Check.isEqual(templates.apply("!ls"), "execute -c | ls");
	}

	@Test
	public void parseTemplates() throws Exception {
		String raw = "$=location -u $${}  \n  !=execute -scf | ${}";
		Map<String, String> templates = EngineTemplates.parseTemplates(raw);
		CheckCollection.isPresent(templates, "$");
		CheckCollection.isPresent(templates, "!");
		CheckStr.isEqual(templates.get("$"), "location -u $${}");
		CheckStr.isEqual(templates.get("!"), "execute -scf | ${}");
	}

	@Test
	public void parseInvalidLine() throws Exception {
		BombSquad.diffuse(AssertionException.class, "invalid", () -> {
			EngineTemplates.parseTemplates("invalid");
		});
	}

	@Test
	public void parseKeyTooLong() throws Exception {
		BombSquad.diffuse(AssertionException.class, "key too long", () -> {
			EngineTemplates.parseTemplates("long=${}");
		});
	}
}