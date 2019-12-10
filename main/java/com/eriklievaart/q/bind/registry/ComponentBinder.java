package com.eriklievaart.q.bind.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.binding.Binding;
import com.eriklievaart.q.bind.binding.BindingInfo;
import com.eriklievaart.q.bind.parse.UiConfig;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class ComponentBinder {
	private final LogTemplate log = new LogTemplate(getClass());

	final Map<String, Binding> bound = new Hashtable<>();
	private Supplier<QMainUi> ui = () -> null;

	public void bindAll(Map<Long, ? extends QUi> services) {
		SwingThread.invokeAndWaitUnchecked(() -> {
			BindingRegistry registry = new BindingRegistry();

			UiConfig config = registry.add(services);
			for (BindingInfo info : config.getBindings()) {
				String key = info.getUniqueKey();
				if (bound.containsKey(key)) {
					continue;
				}
				registry.bind(info).ifPresent(binding -> bound.put(key, binding));
			}
			replaceMenuBar(config, registry);
		});

		if (log.isTraceEnabled()) {
			bound.values().forEach(b -> log.trace("$ -> $", b.component.instance.getName(), b.action.actionId));
		}
	}

	private void replaceMenuBar(UiConfig config, BindingRegistry registry) {
		QMainUi main = ui.get();
		if (main != null) {
			main.setMenuBar(config.createMenuBar(registry));
		}
	}

	public void purgeBundle(long id) {
		log.info("unbinding bundle $", id);
		SwingThread.invokeAndWaitUnchecked(() -> {
			HashMap<String, Binding> copy = new HashMap<>(bound);
			copy.forEach((key, binding) -> {
				if (id == binding.bundleId || id == binding.component.bundleId || id == binding.action.bundleId) {
					unbind(key);
				}
			});
		});
	}

	public void unbindAll() {
		SwingThread.invokeAndWaitUnchecked(() -> {
			for (String key : new ArrayList<>(bound.keySet())) {
				unbind(key);
			}
		});
	}

	private void unbind(String key) {
		Binding binding = bound.get(key);
		binding.event.unbind(binding);
		bound.remove(key);
	}

	public void setMainUiSupplier(Supplier<QMainUi> supplier) {
		this.ui = supplier;
	}
}