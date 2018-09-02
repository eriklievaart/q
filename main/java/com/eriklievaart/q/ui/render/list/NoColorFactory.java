package com.eriklievaart.q.ui.render.list;

import java.awt.Color;

import com.eriklievaart.q.api.render.ColorFactory;

/**
 * Always returns {@link Color#BLACK}.
 * 
 * @author Erik Lievaart
 */
public class NoColorFactory implements ColorFactory {

	@Override
	public Color getColor(final Object object) {
		return Color.black;
	}

}
