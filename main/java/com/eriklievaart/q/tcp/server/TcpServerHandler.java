package com.eriklievaart.q.tcp.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.tcp.TcpDependencies;
import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.q.tcp.shared.TunnelCommand;
import com.eriklievaart.q.tcp.shared.chunk.TcpChunks;
import com.eriklievaart.q.tcp.tunnel.TcpTunnel;
import com.eriklievaart.q.tcp.vfs.TcpFileType;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.io.api.CheckFile;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class TcpServerHandler {
	private LogTemplate log = new LogTemplate(getClass());

	private TcpDependencies dependencies;
	private Map<TunnelCommand, Consumer<TunnelVO>> handlers = new Hashtable<>();

	private TcpTunnel tunnel;

	public TcpServerHandler(TcpDependencies dependencies, TcpTunnel tunnel) {
		this.dependencies = dependencies;
		this.tunnel = tunnel;
		init();
	}

	private void init() {
		handlers.put(TunnelCommand.ROOT, this::commandRoot);
		handlers.put(TunnelCommand.INFO, this::info);
		handlers.put(TunnelCommand.LS, this::ls);
		handlers.put(TunnelCommand.MKDIR, this::mkdir);
		handlers.put(TunnelCommand.DELETE, this::delete);
		handlers.put(TunnelCommand.TO_CLIENT, this::toClient);
		handlers.put(TunnelCommand.TO_SERVER, this::toServer);

		for (TunnelCommand command : TunnelCommand.values()) {
			if (!handlers.containsKey(command)) {
				handlers.put(command, this::logError);
			}
		}
	}

	public void process(TunnelVO request) {
		TunnelCommand command = request.command;
		CheckCollection.isPresent(handlers, command);
		handlers.get(command).accept(request);
	}

	@SuppressWarnings("unused")
	private void commandRoot(TunnelVO request) {
		tunnel.sendVO(new TunnelVO(TunnelCommand.RESPONSE, System.getProperty("user.home")));
	}

	private void ls(TunnelVO request) {
		String path = request.args;
		checkIsValidPath(path);

		VirtualFile file = dependencies.getServiceCollection(UrlResolver.class).oneReturns(r -> r.resolve(path));
		List<? extends VirtualFile> children = file.getChildren();

		TunnelVO response = new TunnelVO(TunnelCommand.RESPONSE);
		response.setBody(ListTool.map(children, c -> getType(c).getShortForm() + " " + c.getName()));
		tunnel.sendVO(response);
	}

	private void info(TunnelVO request) {
		checkIsValidPath(request.args);
		tunnel.sendVO(new TunnelVO(TunnelCommand.RESPONSE, getType(new SystemFile(request.args)).getShortForm()));
	}

	private void mkdir(TunnelVO request) {
		checkIsValidPath(request.args);
		tunnel.sendVO(new TunnelVO(TunnelCommand.RESPONSE, "" + new File(request.args).mkdirs()));
	}

	private void delete(TunnelVO request) {
		checkIsValidPath(request.args);
		File file = new File(request.args);
		boolean exists = file.exists();

		if (exists) {
			ServiceCollection<Engine> engine = dependencies.getServiceCollection(Engine.class);
			engine.oneCall(e -> e.invoke("delete -s `file://" + request.args + "`"));
		}
		tunnel.sendVO(new TunnelVO(TunnelCommand.RESPONSE, "" + exists));
	}

	private void toClient(TunnelVO request) {
		String path = request.args;
		checkIsValidPath(path);
		try {
			TcpChunks.sendChunks(new FileInputStream(path), tunnel);
		} catch (FileNotFoundException e) {
			throw new RuntimeIOException(e);
		}
	}

	private void toServer(TunnelVO request) {
		String path = request.args;
		checkIsValidPath(path);

		File file = new File(path);
		log.debug("downloading to file: $ ", file);
		CheckFile.notExists(file);

		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(path))) {
			TcpChunks.downloadChunks(tunnel, os);
			log.debug("download complete!");

		} catch (IOException e) {
			if (file.exists()) {
				file.delete();
			}
			throw new RuntimeIOException(e);
		}
	}

	private void logError(TunnelVO request) {
		log.warn(request);
	}

	private static void checkIsValidPath(String path) {
		Check.isEmpty(UrlTool.getProtocol(path), "invalid path: " + path);
	}

	private static TcpFileType getType(VirtualFile file) {
		if (!file.exists()) {
			return TcpFileType.MISSING;
		}
		return file.isFile() ? TcpFileType.FILE : TcpFileType.DIRECTORY;
	}
}
