package com.eriklievaart.q.engine.ui;

import java.awt.GridLayout;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QView;
import com.eriklievaart.q.engine.impl.PluginJob;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.swing.api.SwingThread;

public class EngineUi {

	private final EngineSupplierFactory factory;
	private DefaultListModel<PluginJob> model = new DefaultListModel<>();
	public JList<PluginJob> list = new JList<>(model);
	public JPanel panel = new JPanel(new GridLayout(1, 0));
	private AtomicLong count = new AtomicLong();

	public EngineUi(EngineSupplierFactory factory) {
		this.factory = factory;
		panel.add(new JScrollPane(list));
	}

	public Map<String, Consumer<ActionContext>> getActionMap() {
		Map<String, Consumer<ActionContext>> map = NewCollection.map();

		map.put("q.engine.queue.show", c -> {
			QMainUi main = factory.getMainUiSupplier().get();
			if (main != null) {
				QView view = new QView("q.engine.queue", panel);
				view.setLabel("queue");
				main.showView(view);
			}
		});
		return map;
	}

	public void addJob(PluginJob job) {
		count.incrementAndGet();
		SwingThread.invokeLater(() -> {
			model.addElement(job);
		});
	}

	public void completeJob(PluginJob job) {
		count.decrementAndGet();
		SwingThread.invokeLater(() -> {
			for (int i = 0; i < model.getSize(); i++) {
				if (model.getElementAt(i).getId() == job.getId()) {
					model.remove(i);
				}
			}
		});
	}

	public long getQueuedJobCount() {
		return count.get();
	}

}
