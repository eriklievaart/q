package com.eriklievaart.q.tiqqer;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.tiqqer.swing.api.TiqqerFrame;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;

public class TiqqerService implements QUi {

	private ServiceCollection<TiqqerFrame> frame;

	public TiqqerService(ServiceCollection<TiqqerFrame> frame) {
		this.frame = frame;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		return null;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return MapTool.of("q.tiqqer.show", ctx -> frame.oneCall(f -> f.show()));
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/tiqqer/tiqqer-bind.txt");
	}
}
