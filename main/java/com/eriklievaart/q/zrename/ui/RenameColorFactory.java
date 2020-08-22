package com.eriklievaart.q.zrename.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.atomic.AtomicReference;

import com.eriklievaart.q.api.render.ColorFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class RenameColorFactory extends Component implements ColorFactory {

	private AtomicReference<Color> active = new AtomicReference<>(Color.BLACK);
	private AtomicReference<Color> inactive = new AtomicReference<>(Color.GRAY);

	@Override
	public Color getColor(Object object) {
		if (object instanceof RenameListElement) {
			RenameListElement element = (RenameListElement) object;
			return element.isActive() ? active.get() : inactive.get();
		}
		return active.get();
	}

	public void setActiveColor(Color color) {
		Check.notNull(color);
		active.set(color);
	}

	public void setInactiveColor(Color color) {
		Check.notNull(color);
		inactive.set(color);
	}
}
