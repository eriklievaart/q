package com.eriklievaart.q.ui.render.list;

import javax.swing.Icon;

/**
 * Interface that makes it possible to replace the strategy for generating icons for items.
 *
 * @author Erik Lievaart
 */
public interface IconFactory {

	/**
	 * Get the icon for an item.
	 *
	 * @param object
	 *            Can be any class, but the implementation may place restrictions on valid types.
	 */
	public Icon getIcon(Object object);
}
