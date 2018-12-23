package com.eriklievaart.q.ui.render.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.eriklievaart.q.api.render.ColorFactory;

/**
 * Custom {@link ListCellRenderer} for customizing {@link JList} rendering. The foreground color and icons used can be
 * customized using the strategy design pattern. Borders and selection bars are fixed. This class can be shared among
 * JLists.
 *
 * @author Erik Lievaart
 */
public class QListCellRenderer<E> implements ListCellRenderer<E> {

	private static final Color FOCUSED_BORDER = new Color(200, 100, 100);
	private static final Color NORMAL_BORDER = Color.WHITE;
	private static final Color SELECTED_COLOR = new Color(255, 220, 200);

	private ColorFactory foregroundFactory = new NoColorFactory();
	private IconFactory iconFactory = new NoIconFactory();

	/**
	 * Default constructor with {@link NoColorFactory} and {@link NoIconFactory}.
	 */
	public QListCellRenderer() {
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

	private Color getBorderColor(final boolean isSelected, final boolean cellHasFocus) {
		return cellHasFocus ? FOCUSED_BORDER : isSelected ? SELECTED_COLOR : NORMAL_BORDER;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel label = new JLabel(value.toString());

		label.setFont(list.getFont());
		Color border = getBorderColor(isSelected, cellHasFocus);
		label.setBorder(BorderFactory.createLineBorder(border, 1));
		label.setForeground(foregroundFactory.getColor(value));
		if (isSelected) {
			label.setOpaque(true);
			label.setBackground(SELECTED_COLOR);
		}
		label.setIcon(iconFactory.getIcon(value));
		return label;
	}
}
