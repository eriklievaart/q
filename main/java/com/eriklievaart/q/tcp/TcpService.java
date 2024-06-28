package com.eriklievaart.q.tcp;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.tcp.client.TcpClient;
import com.eriklievaart.q.tcp.server.TcpServer;
import com.eriklievaart.q.tcp.ui.TcpController;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class TcpService implements QUi {

	private TcpServer server;
	private TcpClient client;
	private TcpController controller;

	public TcpService(TcpController controller, TcpServer server, TcpClient client) {
		this.controller = controller;
		this.server = server;
		this.client = client;
	}

	@Override
	public Map<String, Component> getComponentMap() {
		Map<String, Component> map = NewCollection.map();

		map.put("q.tcp.panel", controller.panel);
		map.put("q.tcp.list", controller.list);
		return map;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> map = NewCollection.map();
		map.put("q.tcp.server.start", c -> server.startServer());
		map.put("q.tcp.server.stop", c -> server.stopServer());
		map.put("q.tcp.client.connect", c -> client.connect());
		map.put("q.tcp.client.reconnect", c -> client.reconnect());
		map.put("q.tcp.client.disconnect", c -> client.disconnect());
		return map;
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/tcp/tcp-bind.txt");
	}
}
