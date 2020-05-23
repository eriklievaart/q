package com.eriklievaart.q.ui.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class Dialogs {

	private UiComponents components;
	private JList<?> focused;

	public Dialogs(UiComponents components) {
		this.components = components;
	}

	public void input(String message, Consumer<String> consumer) {
		JTextField field = showLabelWithInput(message);
		field.addActionListener(ae -> {
			if (Str.notBlank(field.getText())) {
				consumer.accept(field.getText());
			}
			restoreFocus();
		});
	}

	public void input(String message, String initialValue, Consumer<String> consumer) {
		JTextField field = showLabelWithInput(message);
		field.setText(initialValue);
		field.selectAll();

		field.addActionListener(ae -> {
			if (Str.notBlank(field.getText())) {
				consumer.accept(field.getText());
			}
			restoreFocus();
		});
	}

	public void confirm(String message, Runnable runnable) {
		JButton ok = new JButton("ok");
		JButton cancel = new JButton("cancel");

		actionOnEnter(ok, runAndHide(runnable));
		cancel.addActionListener(ae -> cleanupAndHide());
		showBodyWithButtons(new JLabel(message), Arrays.asList(ok, cancel));
		ok.requestFocus();
	}

	private void showBodyWithButtons(JComponent body, List<JButton> actions) {
		JPanel buttons = new JPanel(new GridLayout(1, 0));
		FocusAdapter hideOnFocusLost = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent event) {
				if (!actions.contains(event.getOppositeComponent())) {
					cleanupAndHide();
				}
			}
		};
		actions.forEach(button -> {
			buttons.add(button);
			button.addFocusListener(hideOnFocusLost);
		});
		components.northPanel.setVisible(false);
		components.northPanel.removeAll();
		components.northPanel.setLayout(new BorderLayout());
		components.northPanel.add(body, BorderLayout.CENTER);
		components.northPanel.add(buttons, BorderLayout.SOUTH);
		components.northPanel.setVisible(true);
	}

	public void message(String message) {
		showComponents(new JLabel(message));
	}

	private JTextField showLabelWithInput(String message) {
		storeFocus();
		JTextField field = new JTextField();
		showComponents(new JLabel(message), field);

		field.requestFocus();
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				cleanupAndHide();
			}
		});
		return field;
	}

	private void storeFocus() {
		JList<VirtualFileWrapper> rightList = components.rightBrowser.fileList;
		focused = rightList.isFocusOwner() ? rightList : components.leftBrowser.fileList;
	}

	private void restoreFocus() {
		focused.requestFocus();
	}

	private void showComponents(JComponent... add) {
		components.northPanel.setVisible(false);
		components.northPanel.removeAll();
		components.northPanel.setLayout(new GridLayout(0, 1));
		for (JComponent component : add) {
			components.northPanel.add(component);
		}
		components.northPanel.setVisible(true);
	}

	private void cleanupAndHide() {
		components.northPanel.removeAll();
		components.northPanel.setVisible(false);
	}

	private void actionOnEnter(JButton button, AbstractAction action) {
		button.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "OK");
		button.getActionMap().put("OK", action);
		button.addActionListener(action::actionPerformed);
	}

	private AbstractAction runAndHide(Runnable runnable) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanupAndHide();
				runnable.run();
			}
		};
		return action;
	}

	private FocusAdapter hideOnFocusLost() {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				cleanupAndHide();
			}
		};
	}
}