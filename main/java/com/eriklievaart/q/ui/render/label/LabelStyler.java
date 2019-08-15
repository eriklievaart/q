package com.eriklievaart.q.ui.render.label;

import java.awt.Font;

import javax.swing.JLabel;

import com.eriklievaart.q.ui.render.Theme;

/**
 * Utility class for quickly styling a label.
 *
 * @author Erik Lievaart
 */
public class LabelStyler {

	private LabelStyler() {
	}

	/**
	 * De-emphasize the label.
	 */
	public static void styleNormal(final JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		label.setForeground(Theme.LABEL_NORMAL);
	}

	/**
	 * De-emphasize the label.
	 */
	public static void styleSubtle(final JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		label.setForeground(Theme.LABEL_SUBTLE);
	}

	/**
	 * Emphasize the label, and style it such that clearly an error occured.
	 */
	public static void styleError(final JLabel label) {
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setForeground(Theme.LABEL_ERROR);
	}
}
