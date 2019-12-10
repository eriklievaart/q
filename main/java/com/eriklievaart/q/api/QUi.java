package com.eriklievaart.q.api;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A bundle that wants their swing Components to be accessible and automatically bound, should publish a service with
 * this interface.
 *
 * @author erikl
 */
public interface QUi {

	/**
	 * List the components provided by this services.
	 *
	 * @return A map with as key the id under which to publish the component, as value the component. The name property
	 *         of the Component will be set by the framework and is not supposed to be changed by the bundle
	 *         implementing the service.
	 */
	public Map<String, Component> getComponentMap();

	/**
	 * List the actions invokable on this service.
	 *
	 * @return A map with as key the id under which to publish the action, as value the action.
	 */
	public Map<String, Consumer<ActionContext>> getActionMap();

	/**
	 * A mapping of the bindings between components and actions. The data should be in IniNode format.
	 */
	public InputStream getBindings();
}