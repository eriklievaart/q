package com.eriklievaart.q.api.render;

import java.awt.Color;

/**
 * Interface that makes it possible to replace the strategy for coloring items.
 * 
 * @author Erik Lievaart
 */
public interface ColorFactory {

	/**
	 * Get the color for an item.
	 * 
	 * @param object
	 *            Can be any class, but the implementation may place restrictions on valid types.
	 */
	public Color getColor(Object object);
}
