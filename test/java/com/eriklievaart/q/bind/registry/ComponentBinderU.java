package com.eriklievaart.q.bind.registry;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;

import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;

public class ComponentBinderU {

	private ComponentBinder testable;

	@Before
	public void init() {
		testable = new ComponentBinder();
	}

	@Test
	public void installMouseListener() {
		JList<String> list = new JList<>();
		final AtomicBoolean clicked = new AtomicBoolean();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.event("CLICK1_TWICE").action("2click"));
		ui.putComponent("list.id", list);
		ui.putAction("2click", c -> clicked.set(true));

		Check.isEqual(list.getMouseListeners().length, 2);
		Check.isFalse(clicked.get());
		testable.bindAll(MapTool.of(2l, ui));
		Check.isEqual(list.getMouseListeners().length, 3);
		MouseEvent event = new MouseEvent(list, 0, System.currentTimeMillis(), 0, 0, 0, 0, 0, 2, false, 1);

		for (MouseListener listener : list.getMouseListeners()) {
			listener.mouseClicked(event);
		}
		Check.isTrue(clicked.get());
	}

	@Test
	public void installListSelectionListener() {
		JList<String> list = new JList<>();
		final AtomicBoolean selected = new AtomicBoolean();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.event("SELECTION").action("select"));
		ui.putComponent("list.id", list);
		ui.putAction("select", c -> selected.set(true));

		Check.isEqual(list.getListSelectionListeners().length, 0);
		Check.isFalse(selected.get());
		testable.bindAll(MapTool.of(2l, ui));
		Check.isEqual(list.getListSelectionListeners().length, 1);

		list.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(list, 0, 0, false));
		Check.isTrue(selected.get());
	}

	@Test
	public void installMissingAction() {
		JList<String> list = new JList<>();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.event("SELECTION").action("select"));
		ui.putComponent("list.id", list);

		Check.isEqual(list.getListSelectionListeners().length, 0);
		testable.bindAll(MapTool.of(2l, ui));
		Check.isEqual(list.getListSelectionListeners().length, 0);

	}

	@Test
	public void installMissingComponent() {
		final AtomicBoolean selected = new AtomicBoolean();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.event("SELECTION").action("select"));
		ui.putAction("select", c -> selected.set(true));

		Check.isFalse(selected.get());
		testable.bindAll(MapTool.of(2l, ui));
		CheckCollection.isEmpty(testable.bound);
	}

	@Test
	public void installKeyListener() {
		JList<String> list = new JList<>();
		final AtomicBoolean clicked = new AtomicBoolean();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.keyReleased("ctrl UP").action("key"));
		ui.putComponent("list.id", list);
		ui.putAction("key", c -> clicked.set(true));

		Check.isEqual(list.getKeyListeners().length, 2);
		Check.isFalse(clicked.get());
		testable.bindAll(MapTool.of(2l, ui));
		Check.isEqual(list.getKeyListeners().length, 3);

		int mask = KeyEvent.CTRL_DOWN_MASK;
		KeyEvent event = new KeyEvent(list, KeyEvent.KEY_RELEASED, 0, mask, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
		for (KeyListener listener : list.getKeyListeners()) {
			listener.keyReleased(event);
		}
		Check.isTrue(clicked.get());
	}

	@Test
	public void installBindListSelectionListenerToJPanel() {
		JPanel panel = new JPanel();

		StubUi ui = ComponentNodeBuilder.create("panel.id", binding -> binding.event("SELECTION").action("event"));
		ui.putComponent("panel.id", panel);
		ui.putAction("event", System.out::println);

		testable.bindAll(MapTool.of(2l, ui));
		CheckCollection.isEmpty(testable.bound);
	}

	@Test
	public void purgeBundleId() {
		final AtomicBoolean hasBeenInvoked = new AtomicBoolean();
		JList<String> list = new JList<>();

		StubUi ui = ComponentNodeBuilder.create("list.id", binding -> binding.event("SELECTION").action("event"));
		ui.putComponent("list.id", list);
		ui.putAction("event", c -> hasBeenInvoked.set(true));

		// test that the binding works
		Check.isFalse(hasBeenInvoked.get());
		checkListHasNoListeners(list);
		testable.bindAll(MapTool.of(2l, ui));
		list.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(list, 0, 0, false));
		Check.isTrue(hasBeenInvoked.get());
		CheckCollection.notEmpty(testable.bound);

		// purge binding and test that it no longer works
		hasBeenInvoked.set(false);
		testable.purgeBundle(2l);
		checkListHasNoListeners(list);
		CheckCollection.isEmpty(testable.bound);
	}

	private void checkListHasNoListeners(JList<String> list) {
		Check.isEqual(list.getListSelectionListeners().length, 0);
	}
}