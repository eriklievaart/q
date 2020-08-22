package com.eriklievaart.q.api.render;

import java.awt.Color;
import java.awt.Component;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class JListThemed<E> extends JList<E> implements ListCellRenderer<E> {

	private AtomicReference<Color> fgNormal = new AtomicReference<>(Color.BLACK);
	private AtomicReference<Color> fgDirectory = new AtomicReference<>(Color.BLUE);
	private AtomicReference<Color> bgNormal = new AtomicReference<>(Color.WHITE);
	private AtomicReference<Color> bgSelected = new AtomicReference<>(Color.GRAY);
	private AtomicReference<Color> borderNormal = new AtomicReference<>(Color.RED);
	private AtomicReference<Color> borderFocused = new AtomicReference<>(Color.RED);
	private AtomicReference<Color> borderSelected = new AtomicReference<>(Color.RED);

	protected ColorFactory foregroundFactory = object -> fgNormal.get();
	protected IconFactory iconFactory = object -> null;

	public JListThemed() {
		setCellRenderer(this);
	}

	public JListThemed(E[] data) {
		super(data);
		setCellRenderer(this);
	}

	public JListThemed(ListModel<E> model) {
		super(model);
		setCellRenderer(this);
	}

	/**
	 * Replace the strategy for determining the text color.
	 */
	public void setForegroundFactory(final ColorFactory factory) {
		this.foregroundFactory = factory;
	}

	/**
	 * Replace the strategy for determining the icon.
	 */
	public void setIconFactory(final IconFactory factory) {
		this.iconFactory = factory;
	}

	public Color getForegroundDirectory() {
		return fgDirectory.get();
	}

	public Color getForegroundNormal() {
		return fgNormal.get();
	}

	public void setForegroundNormal(Color color) {
		Check.notNull(color);
		fgNormal.set(color);
	}

	public void setForegroundDirectory(Color color) {
		Check.notNull(color);
		fgDirectory.set(color);
	}

	public void setBackgroundNormal(Color color) {
		Check.notNull(color);
		bgNormal.set(color);
		setBackground(color);
	}

	public void setBackgroundSelected(Color color) {
		Check.notNull(color);
		bgSelected.set(color);
	}

	public void setBorderNormal(Color color) {
		Check.notNull(color);
		borderNormal.set(color);
	}

	public void setBorderSelected(Color color) {
		Check.notNull(color);
		borderSelected.set(color);
	}

	public void setBorderFocused(Color color) {
		Check.notNull(color);
		borderFocused.set(color);
	}

	private Color getBorderColor(final boolean isSelected, final boolean cellHasFocus) {
		return cellHasFocus ? borderFocused.get() : isSelected ? borderSelected.get() : borderNormal.get();
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = new JLabel(value.toString());

		label.setFont(list.getFont());
		Color border = getBorderColor(isSelected, cellHasFocus);
		label.setBorder(BorderFactory.createLineBorder(border, 1));
		label.setForeground(foregroundFactory.getColor(value));
		label.setOpaque(true);
		label.setBackground(isSelected ? bgSelected.get() : bgNormal.get());
		label.setIcon(iconFactory.getIcon(value));
		return label;
	}
}