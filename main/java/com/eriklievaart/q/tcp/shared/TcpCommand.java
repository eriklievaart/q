package com.eriklievaart.q.tcp.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.tcp.TcpDependencies;
import com.eriklievaart.q.tcp.vfs.TcpFileType;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.io.api.CheckFile;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.io.api.sha1.Sha1InputStream;
import com.eriklievaart.toolkit.io.api.sha1.Sha1OutputStream;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public enum TcpCommand {

	LS {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			checkIsValidPath(path);
			VirtualFile file = dependencies.getServiceCollection(UrlResolver.class).oneReturns(r -> r.resolve(path));
			try {
				List<? extends VirtualFile> children = file.getChildren();
				transfer.writeLines(ListTool.map(children, c -> getType(c).getShortForm() + " " + c.getName()));

			} catch (Exception e) {
				transfer.writeInt(0);
				new LogTemplate(TcpCommand.class).debug(e);
			}
		}
	},

	INFO {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			checkIsValidPath(path);
			transfer.writeString(getType(new SystemFile(path)).getShortForm());
		}
	},

	ROOT {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String unused) {
			transfer.writeString(System.getProperty("user.home"));
		}
	},

	MKDIR {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			checkIsValidPath(path);
			boolean result = new File(path).mkdirs();
			transfer.writeBoolean(result);
		}
	},

	DELETE {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			checkIsValidPath(path);
			File file = new File(path);
			boolean exists = file.exists();
			ServiceCollection<Engine> engine = dependencies.getServiceCollection(Engine.class);
			transfer.writeBoolean(exists);
			engine.oneCall(e -> e.invoke("delete -s `file://" + path + "`"));
		}
	},

	TO_SERVER {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			LogTemplate log = new LogTemplate(TcpCommand.class);
			checkIsValidPath(path);

			File file = new File(path);
			log.debug("downloading to file: $ ", file);
			try {
				CheckFile.notExists(file);
				transfer.download(new Sha1OutputStream(new FileOutputStream(path)));
				log.debug("download complete!");

			} catch (IOException e) {
				if (file.exists()) {
					file.delete();
				}
				throw new RuntimeIOException(e);
			}
		}
	},

	TO_CLIENT {
		@Override
		public void invoke(TcpDependencies dependencies, TcpTransfer transfer, String path) {
			checkIsValidPath(path);
			try {
				transfer.upload(new Sha1InputStream(new FileInputStream(path)));
			} catch (FileNotFoundException e) {
				throw new RuntimeIOException(e);
			}
		}
	};

	public abstract void invoke(TcpDependencies dependencies, TcpTransfer transfer, String args);

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
