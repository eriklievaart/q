package com.eriklievaart.q.bind;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import com.eriklievaart.q.api.QUi;
import com.eriklievaart.q.bind.registry.ComponentBinder;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class BindServiceListener implements ServiceListener {
	private LogTemplate log = new LogTemplate(getClass());

	private ComponentBinder binder;
	private BundleContext context;

	public BindServiceListener(ComponentBinder binder) {
		this.binder = binder;
	}

	public void setBundleContext(BundleContext context) {
		this.context = context;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		long bundleId = event.getServiceReference().getBundle().getBundleId();
		log.debug("$) Service Listener event: $", bundleId, event);

		Object object = context.getService(event.getServiceReference());
		if (object instanceof QUi) {
			if (event.getType() == ServiceEvent.REGISTERED) {
				binder.bindAll(getAllServices());
			}
			if (event.getType() == ServiceEvent.UNREGISTERING) {
				binder.purgeBundle(bundleId);
			}
		} else {
			log.debug("$) not assignable", event.getServiceReference().getBundle().getBundleId());
		}
	}

	public Map<Long, QUi> getAllServices() {
		Map<Long, QUi> services = NewCollection.map();
		try {
			context.getServiceReferences(QUi.class, null).forEach(r -> {
				services.put(r.getBundle().getBundleId(), context.getService(r));
			});
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
		return services;
	}
}