package com.eriklievaart.q.ui.main;

import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.event.EngineEvent;

public class CommandActions {

	private UiBeanFactory beans;
	private EngineEvent engine;

	public CommandActions(UiBeanFactory beans) {
		this.beans = beans;
		this.engine = beans.getEngineEvent();
	}

	public void putActions(Map<String, Consumer<ActionContext>> map) {
		map.put("q.command.execute", c -> engine.executeTemplated(beans.getComponents().commandField.getText()));
		map.put("q.command.assist", c -> engine.validate(beans.getComponents().commandField.getText()));
		map.put("q.command.focus", c -> focusCommandLine());
	}

	private void focusCommandLine() {
		UiComponents components = beans.getComponents();
		components.southPanel.add(components.commandField);
		components.commandField.selectAll();
		components.commandField.requestFocus();
		components.mainFrame.validate();
	}
}