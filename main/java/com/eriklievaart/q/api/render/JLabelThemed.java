package com.eriklievaart.q.api.render;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JLabel;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class JLabelThemed extends JLabel {

	private AtomicReference<Color> normal = new AtomicReference<>(Color.BLACK);
	private AtomicReference<Color> subtle = new AtomicReference<>(Color.GRAY);
	private AtomicReference<Color> invalid = new AtomicReference<>(Color.RED);

	private State state = State.NORMAL;

	public JLabelThemed() {
	}

	public JLabelThemed(String text) {
		super(text);
	}

	public void setNormalColor(Color color) {
		Check.notNull(color);
		normal.set(color);
		if (state == State.NORMAL) {
			setForeground(color);
		}
	}

	public void setSubtleColor(Color color) {
		Check.notNull(color);
		subtle.set(color);
		if (state == State.SUBTLE) {
			setForeground(color);
		}
	}

	public void setInvalidColor(Color color) {
		Check.notNull(color);
		invalid.set(color);
		if (state == State.INVALID) {
			setForeground(color);
		}
	}

	public void setNormalState() {
		state = State.NORMAL;
		setForeground(normal.get());
		setFont(getFont().deriveFont(Font.PLAIN));
	}

	public void setSubtleState() {
		state = State.SUBTLE;
		setForeground(subtle.get());
		setFont(getFont().deriveFont(Font.PLAIN));
	}

	public void setInvalidState() {
		state = State.INVALID;
		setForeground(invalid.get());
		setFont(getFont().deriveFont(Font.BOLD));
	}

	public boolean isNormalState() {
		return state == State.NORMAL;
	}

	public boolean isSubtleState() {
		return state == State.SUBTLE;
	}

	public boolean isInvalidState() {
		return state == State.INVALID;
	}

	private enum State {
		NORMAL, SUBTLE, INVALID
	}
}
