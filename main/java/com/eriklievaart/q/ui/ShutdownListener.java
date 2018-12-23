package com.eriklievaart.q.ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

public class ShutdownListener implements WindowListener {
	private AtomicBoolean shutdownOnCloseWindow = new AtomicBoolean(true);
	private BundleContext context;
	private UiBeanFactory beans;

	public ShutdownListener(BundleContext context, UiBeanFactory beans) {
		this.context = context;
		this.beans = beans;
	}

	public void setOsgiFrameworkShutdown(boolean value) {
		shutdownOnCloseWindow.set(value);
	}

	@Override
	public void windowOpened(WindowEvent we) {
	}

	@Override
	public void windowClosing(WindowEvent we) {
		if (!shutdownOnCloseWindow.get()) {
			System.out.println("closing main window only");
			return;
		}
		long queue = beans.getEngineEvent().getQueuedJobCount();
		if (queue > 0) {
			String msg = queue + " jobs queued. really quit?";
			int result = JOptionPane.showConfirmDialog(null, msg, "input", JOptionPane.OK_CANCEL_OPTION);
			if (result != JOptionPane.OK_OPTION) {
				return;
			}
		}
		new Thread(() -> shutdown()).start();
	}

	private void shutdown() {
		try {
			Framework systemBundle = context.getBundle(0).adapt(Framework.class);
			systemBundle.stop();
			System.out.println("Waiting up to 2s for OSGi shutdown to complete...");
			systemBundle.waitForStop(2000);

		} catch (Exception e) {
			System.err.println("Failed to cleanly shutdown OSGi Framework: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void windowClosed(WindowEvent we) {
	}

	@Override
	public void windowIconified(WindowEvent we) {
	}

	@Override
	public void windowDeiconified(WindowEvent we) {
	}

	@Override
	public void windowActivated(WindowEvent we) {
	}

	@Override
	public void windowDeactivated(WindowEvent we) {
	}
}
