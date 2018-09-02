package com.eriklievaart.q.bind.binding;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;

import com.eriklievaart.q.bind.registry.ListenerFactory;

public enum ActionType {

	WINDOW_CLOSING(JFrame.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindWindowClosing(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindWindowListener(binding);
		}
	},
	ACTION(JButton.class, JTextField.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindActionListener(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindActionListener(binding);
		}
	},
	FOCUS_LOST(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindFocusLostListener(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindFocusListener(binding);
		}

	},
	FOCUS_GAINED(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindFocusGainedListener(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindFocusListener(binding);
		}
	},
	CLICK1(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindMouseListenerClick(binding, e -> {
				return e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1;
			});
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindMouseListener(binding);
		}

	},
	CLICK2(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindMouseListenerClick(binding, e -> {
				return e.getButton() == MouseEvent.BUTTON2 && e.getClickCount() == 1;
			});
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindMouseListener(binding);
		}
	},
	CLICK3(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindMouseListenerClick(binding, e -> {
				return e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1;
			});
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindMouseListener(binding);
		}
	},
	CLICK1_TWICE(Component.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindMouseListenerClick(binding, e -> {
				return e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2;
			});
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindMouseListener(binding);
		}
	},
	SELECTION(JList.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindListSelectionListener(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindListSelectionListener(binding);
		}
	},
	KEY_RELEASED(JComponent.class) {
		@Override
		public Object bind(final Binding binding) {
			return ListenerFactory.bindKeyListenerReleased(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindKeyListener(binding);
		}
	},
	KEY_PRESSED(JComponent.class) {
		@Override
		public Object bind(Binding binding) {
			return ListenerFactory.bindKeyListenerPressed(binding);
		}

		@Override
		public void unbind(Binding binding) {
			ListenerFactory.unbindKeyListener(binding);
		}
	};

	private final Class<?>[] accepts;

	private ActionType(Class<?>... accepts) {
		this.accepts = accepts;
	}

	public boolean isValidComponent(Component component) {
		for (Class<?> clz : accepts) {
			if (clz.isAssignableFrom(component.getClass())) {
				return true;
			}
		}
		return false;
	}

	public abstract Object bind(final Binding binding);

	public abstract void unbind(Binding binding);

}
