package com.eriklievaart.q.api.engine;

import java.util.Map;

import com.eriklievaart.toolkit.lang.api.concurrent.Prototype;

/**
 * The context the plug in was invoked under. Contains configuration and state information that might be required by a
 * plug in.
 *
 * @author Erik Lievaart
 */
@Prototype
public interface PluginContext {

	public String getPipedContents();

	public String getVariable(String value);

	public Map<String, String> getVariables();
}