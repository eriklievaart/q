package com.eriklievaart.q.doc.generate;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.q.engine.meta.CommandMetadata;

public class CommandContext {

	public String command;
	public String doc;
	public CommandMetadata metadata;
	public Map<String, String> flagDoc = new Hashtable<>();
	public Map<String, String> examples = new Hashtable<>();
	public String flagless;
	public String piped;
	public List<String> plugins;
	public String description;

	public CommandContext(String name) {
		this.command = name;
	}
}