package com.eriklievaart.q.bind.registry;

import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.binding.ActionWrapper;
import com.eriklievaart.q.bind.binding.Binding;
import com.eriklievaart.q.bind.binding.BindingInfo;
import com.eriklievaart.q.bind.binding.ComponentWrapper;
import com.eriklievaart.q.bind.parse.ConfigParser;
import com.eriklievaart.q.bind.parse.UiConfig;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class BindingRegistry {
	private final LogTemplate log = new LogTemplate(getClass());
	private final String idRegex = "[-._a-z0-9]++";
	private final Pattern idPattern = Pattern.compile(idRegex);

	Map<String, ComponentWrapper> components = new Hashtable<>();
	Map<String, ActionWrapper> actions = new Hashtable<>();

	public UiConfig add(Map<Long, ? extends QUi> services) {
		ConfigParser parser = new ConfigParser();

		services.forEach((bundleId, ui) -> {
			try {
				validate(ui);
				parser.parse(bundleId, ui.getBindings());
				addComponents(bundleId, ui);
				addActions(bundleId, ui);

			} catch (Exception e) {
				log.warn("not binding bundle %; $", e, bundleId, e.getMessage());
			}
		});
		return parser.getConfig();
	}

	public void addComponents(long bundleId, QUi ui) {
		if (ui.getComponentMap() != null) {
			ui.getComponentMap().forEach((componentId, component) -> {
				ComponentWrapper wrapper = new ComponentWrapper(bundleId, component);
				component.setName(componentId);
				components.put(componentId, wrapper);
			});
		}
	}

	public ActionListener createActionListener(String action) {
		Check.notBlank(action);
		CheckCollection.isPresent(actions, action);
		return ListenerFactory.createActionListener(actions.get(action));
	}

	public void addActions(long bundleId, QUi ui) {
		ui.getActionMap().forEach((actionId, action) -> {
			putAction(actionId, new ActionWrapper(bundleId, actionId, action));
		});
	}

	public void putAction(String actionId, ActionWrapper wrapper) {
		actions.put(actionId, wrapper);
	}

	public Optional<Binding> bind(BindingInfo info) {
		if (!components.containsKey(info.componentId)) {
			info.warn("component not available");
			return Optional.empty();
		}
		if (!actions.containsKey(info.actionId)) {
			info.warn("action not available");
			return Optional.empty();
		}
		ComponentWrapper component = components.get(info.componentId);

		if (!info.event.isValidComponent(component.instance)) {
			String clz = component.instance.getClass().getSimpleName();
			log.warn("action % of type $ cannot be assigned to $[$]", info.actionId, info.event, clz, info.componentId);
			return Optional.empty();
		}
		return Optional.of(new Binding(info, component, actions.get(info.actionId)));
	}

	void validate(QUi ui) {
		if (ui.getComponentMap() != null) {
			ui.getComponentMap().keySet().forEach(key -> {
				CheckCollection.notPresent(components, key, "Duplicate component %", key);
				validateId(key);
			});
		}
		if (ui.getActionMap() != null) {
			ui.getActionMap().keySet().forEach(key -> {
				CheckCollection.notPresent(actions, key, "Duplicate action %", key);
				validateId(key);
			});
		}
	}

	private void validateId(String key) {
		Check.isTrue(idPattern.matcher(key).matches(), "Invalid key %, must match $", key, idRegex);
	}
}
