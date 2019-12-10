package com.eriklievaart.q.zexecute;

import java.awt.Color;

import com.eriklievaart.q.api.render.ColorFactory;

public class ExecuteColorFactory implements ColorFactory {

	@Override
	public Color getColor(Object object) {
		if (object instanceof TerminalLine) {
			TerminalLine line = (TerminalLine) object;
			return line.isError() ? Color.RED : Color.WHITE;
		}
		return Color.BLACK;
	}
}