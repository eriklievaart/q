package com.eriklievaart.q.engine;

import java.awt.Component;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.engine.api.Engine;
import com.eriklievaart.q.engine.api.EngineResult;
import com.eriklievaart.q.engine.concurrent.CommandScheduler;
import com.eriklievaart.q.engine.impl.PluginContextImpl;
import com.eriklievaart.q.engine.impl.PluginJob;
import com.eriklievaart.q.engine.meta.CommandMetadata;
import com.eriklievaart.q.engine.osgi.EngineSupplierFactory;
import com.eriklievaart.q.engine.parse.ShellCommand;
import com.eriklievaart.q.engine.parse.ShellParser;
import com.eriklievaart.q.engine.ui.EngineUi;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.IdGenerator;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class EngineService implements Engine, QUi {
	private LogTemplate log = new LogTemplate(getClass());

	private final IdGenerator generator = new IdGenerator();
	private final CommandScheduler scheduler;

	private EngineSupplierFactory factory;

	public EngineService(EngineSupplierFactory factory) {
		this.factory = factory;
		this.scheduler = new CommandScheduler(factory);
	}

	@Override
	public void invoke(String raw) {
		log.info("invoking: " + raw);

		try {
			ShellCommand command = ShellParser.parseLine(raw);
			PluginContextImpl context = factory.getPluginContext(command);
			EngineResult result = factory.getInputExaminer().examineParsed(command, context);

			AssertionException.on(result.isError(), "invalid: " + result.getMessage());

			PluginJob job = new PluginJob(generator.next(), command, context);
			job.setLabel(raw);
			CommandMetadata metadata = factory.getPluginIndex().lookup(command.getName()).get();
			scheduler.schedule(job, metadata.getPlugin().getThreadPolicy());

		} catch (Exception e) {
			throw new RuntimeException("error: " + e.getMessage(), e);
		}
	}

	@Override
	public EngineResult parse(String verify) {
		return factory.getInputExaminer().examineRaw(verify);
	}

	@Override
	public void invokeTemplated(String raw) {
		invoke(factory.getTemplates().apply(raw));
	}

	@Override
	public EngineResult parseTemplated(String verify) {
		return parse(factory.getTemplates().apply(verify));
	}

	@Override
	public Map<String, Component> getComponentMap() {
		EngineUi ui = factory.getEngineUi();

		Map<String, Component> map = NewCollection.map();
		map.put("q.engine.queue.list", ui.list);
		map.put("q.engine.queue.panel", ui.panel);
		return map;
	}

	@Override
	public Map<String, Consumer<ActionContext>> getActionMap() {
		return factory.getEngineUi().getActionMap();
	}

	@Override
	public InputStream getBindings() {
		return getClass().getResourceAsStream("/engine/engine-bindings.txt");
	}

	@Override
	public long getQueuedJobCount() {
		return factory.getEngineUi().getQueuedJobCount();
	}
}