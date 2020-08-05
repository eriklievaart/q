package com.eriklievaart.q.laf;

import com.eriklievaart.osgi.toolkit.api.listener.SimpleServiceListener;
import com.eriklievaart.q.api.QUi;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class LafListener implements SimpleServiceListener<QUi> {
	private LogTemplate log = new LogTemplate(getClass());

	private final UiSettings settings;

	public LafListener(UiSettings settings) {
		this.settings = settings;
	}

	@Override
	public void register(QUi service) {
		try {
			log.info("applying LAF for " + service);
			if (service.getComponentMap() != null) {
				settings.apply(service.getComponentMap().values());
			}
		} catch (Exception e) {
			log.error("Unable to set LAF for $", e, service.getClass());
		}
	}

	@Override
	public void unregistering(QUi service) {
	}
}
