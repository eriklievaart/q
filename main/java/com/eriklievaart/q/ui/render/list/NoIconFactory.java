package com.eriklievaart.q.ui.render.list;

import javax.swing.Icon;

/**
 * Always returns null (no icon).
 *
 * @author Erik Lievaart
 */
public class NoIconFactory implements IconFactory {

	@Override
	public Icon getIcon(final Object object) {
		return null;
	}
}