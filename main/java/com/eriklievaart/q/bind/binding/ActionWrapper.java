package com.eriklievaart.q.bind.binding;

import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ActionWrapper implements Consumer<ActionContext> {

	public final long bundleId;
	public final String actionId;
	private final Consumer<ActionContext> consumer;

	public ActionWrapper(long bundleId, String actionId, Consumer<ActionContext> action) {
		Check.notNull(action);
		this.bundleId = bundleId;
		this.actionId = actionId;
		this.consumer = action;
	}

	@Override
	public void accept(ActionContext context) {
		Check.notNull(consumer, "Action does not exist!");
		consumer.accept(context);
	}
}