package com.eriklievaart.q.tcp;

import java.io.File;

import com.eriklievaart.osgi.toolkit.api.ContextWrapper;
import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.tcp.client.TcpClient;
import com.eriklievaart.q.tcp.client.TcpHosts;
import com.eriklievaart.q.tcp.server.TcpServer;
import com.eriklievaart.q.tcp.ui.TcpController;
import com.eriklievaart.q.tcp.vfs.TcpProtocolResolver;
import com.eriklievaart.q.ui.api.QMainUi;

public class TcpDependencies {

	private ContextWrapper wrapper;

	public TcpProtocolResolver protocols;
	public TcpService service;
	public TcpServer server;
	public TcpClient client;
	public TcpController controller;

	public TcpDependencies(ContextWrapper wrapper) {
		this.wrapper = wrapper;
		initDependencies();
	}

	private void initDependencies() {
		ServiceCollection<QMainUi> ui = wrapper.getServiceCollection(QMainUi.class);

		controller = new TcpController(ui);
		client = new TcpClient(new TcpHosts(hostsConfig()), ui);
		server = new TcpServer(this);
		service = new TcpService(controller, server, client);
		protocols = new TcpProtocolResolver(client);
	}

	public <E> ServiceCollection<E> getServiceCollection(Class<E> type) {
		return wrapper.getServiceCollection(type);
	}

	private File hostsConfig() {
		return new File(wrapper.getBundleParentDir(), "data/tcp/hosts.txt");
	}
}
