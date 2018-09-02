package com.eriklievaart.q.ui.event;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class EngineEvent {
	private static final long INACTIVITY_TIMEOUT = 500;

	private AtomicBoolean active = new AtomicBoolean();
	private AtomicLong last = new AtomicLong();
	private AtomicReference<String> command = new AtomicReference<>();

	private Supplier<Engine> engine;
	private Consumer<EngineResult> parseResultConsumer;

	public EngineEvent(Supplier<Engine> supplier) {
		this.engine = supplier;
	}

	public long getQueuedJobCount() {
		Engine instance = engine.get();
		return instance == null ? 0 : instance.getQueuedJobCount();
	}

	public void setParseResultConsumer(Consumer<EngineResult> parseResultConsumer) {
		this.parseResultConsumer = parseResultConsumer;
	}

	public void executeTemplated(String text) {
		getEngine().ifPresent(e -> e.invokeTemplated(text));
	}

	public void executeRaw(String text) {
		getEngine().ifPresent(e -> e.invoke(text));
	}

	public Optional<Engine> getEngine() {
		Engine e = engine.get();
		if (e == null) {
			JOptionPane.showMessageDialog(null, "Command line interpreter unavailable!");
		}
		return e == null ? Optional.empty() : Optional.of(e);
	}

	public void validate(String text) {
		String previous = command.get();
		Check.notNull(text);
		last.set(System.currentTimeMillis());
		command.set(text);
		boolean wasInactive = active.compareAndSet(false, true);
		if (wasInactive) {
			new Thread(new EventRunnable(previous)).start();
		}
	}

	private class EventRunnable implements Runnable {
		private String onStatusLine;

		public EventRunnable(String previous) {
			this.onStatusLine = previous;
		}

		@Override
		public void run() {
			try {
				while (true) {
					long inactive = System.currentTimeMillis() - last.get();

					if (inactive < INACTIVITY_TIMEOUT) {
						long sleep = INACTIVITY_TIMEOUT - inactive;
						Thread.sleep(sleep);

					} else {
						parseLine();
						if (System.currentTimeMillis() - last.get() > INACTIVITY_TIMEOUT) {
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				active.set(false);
			}
		}

		private void parseLine() {
			String verify = command.get().trim();
			if (!verify.equals(onStatusLine)) {
				Engine e = engine.get();
				if (e != null) {
					parseResultConsumer.accept(e.parseTemplated(verify));
				}
			}
		}
	}

}
