package com.eriklievaart.q.bind.registry;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.bind.binding.ActionWrapper;
import com.eriklievaart.q.bind.binding.Binding;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class ListenerFactory {

	private static void invoke(ActionWrapper action, Object event) {
		try {
			new LogTemplate(ListenerFactory.class).trace("invoking action %", action.actionId);
			action.accept(new ActionContext() {
				@Override
				public Object getEventObject() {
					return event;
				}
			});

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "*error*: " + e.getMessage());
			new LogTemplate(action.getClass()).warn("Exception occurred during $", e, action);
		}
	}

	public static ActionListener createActionListener(ActionWrapper action) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invoke(action, e);
			}
		};
	}

	public static WindowAdapter bindWindowClosing(Binding binding) {
		Check.notNull(binding.action);
		WindowAdapter listener = new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				invoke(binding.action, e);
			}
		};
		JFrame frame = (JFrame) binding.component.instance;
		frame.addWindowListener(listener);
		return listener;
	}

	public static ActionListener bindActionListener(Binding binding) {
		Check.notNull(binding.action);
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invoke(binding.action, e);
			}
		};
		if (binding.component.instance instanceof JButton) {
			JButton button = (JButton) binding.component.instance;
			button.addActionListener(listener);
		}
		if (binding.component.instance instanceof JTextField) {
			JTextField field = (JTextField) binding.component.instance;
			field.addActionListener(listener);
		}
		return listener;
	}

	public static FocusListener bindFocusLostListener(Binding binding) {
		Check.notNull(binding.action);
		FocusListener listener = new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				invoke(binding.action, e);
			}
		};
		binding.component.instance.addFocusListener(listener);
		return listener;
	}

	public static FocusListener bindFocusGainedListener(Binding binding) {
		Check.notNull(binding.action);
		FocusListener listener = new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				invoke(binding.action, e);
			}
		};
		binding.component.instance.addFocusListener(listener);
		return listener;
	}

	public static MouseListener bindMouseListenerClick(Binding binding, Predicate<MouseEvent> click) {
		Check.notNull(binding.action);
		MouseListener listener = new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (click.test(e)) {
					invoke(binding.action, e);
				}
			}
		};
		binding.component.instance.addMouseListener(listener);
		return listener;
	}

	public static ListSelectionListener bindListSelectionListener(Binding binding) {
		Check.notNull(binding.action);
		ListSelectionListener listener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				invoke(binding.action, e);
			}
		};
		JList<?> list = (JList<?>) binding.component.instance;
		list.addListSelectionListener(listener);
		return listener;
	}

	public static KeyListener bindKeyListenerReleased(Binding binding) {
		Check.notNull(binding.action);
		KeyListener listener = new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent event) {
				invokeKey(event, binding, binding.keyReleased);
			}
		};
		JComponent jc = (JComponent) binding.component.instance;
		jc.addKeyListener(listener);
		return listener;
	}

	public static KeyListener bindKeyListenerPressed(Binding binding) {
		Check.notNull(binding.action);
		KeyListener listener = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent event) {
				invokeKey(event, binding, binding.keyPressed);
			}
		};
		JComponent jc = (JComponent) binding.component.instance;
		jc.addKeyListener(listener);
		return listener;
	}

	private static void invokeKey(KeyEvent event, Binding binding, KeyStroke key) {
		KeyStroke e = KeyStroke.getKeyStrokeForEvent(event);
		if (key == null) {
			invoke(binding.action, event);
			return;
		}
		boolean sameChar = e.getKeyChar() == key.getKeyChar();
		boolean sameCode = e.getKeyCode() == key.getKeyCode();
		boolean sameModifiers = e.getModifiers() == key.getModifiers();
		if (sameChar && sameCode && sameModifiers) {
			invoke(binding.action, event);
		}
	}

	public static void unbindMouseListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		binding.component.instance.removeMouseListener((MouseListener) binding.bound);
	}

	public static void unbindFocusListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		binding.component.instance.removeFocusListener((FocusListener) binding.bound);
	}

	public static void unbindWindowListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		JFrame frame = (JFrame) binding.component.instance;
		frame.removeWindowListener((WindowListener) binding.bound);
	}

	public static void unbindActionListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		if (binding.component.instance instanceof JButton) {
			JButton button = (JButton) binding.component.instance;
			button.removeActionListener((ActionListener) binding.bound);
		}
		if (binding.component.instance instanceof JTextField) {
			JTextField field = (JTextField) binding.component.instance;
			field.removeActionListener((ActionListener) binding.bound);
		}
	}

	public static void unbindListSelectionListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		JList<?> list = (JList<?>) binding.component.instance;
		list.removeListSelectionListener((ListSelectionListener) binding.bound);
	}

	public static void unbindKeyListener(Binding binding) {
		Check.notNull(binding.component.instance, binding.bound);
		JComponent component = (JComponent) binding.component.instance;
		component.removeKeyListener((KeyListener) binding.bound);
	}

}
