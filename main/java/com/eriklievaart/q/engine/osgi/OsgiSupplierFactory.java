package com.eriklievaart.q.engine.osgi;

import java.util.List;
import java.util.function.Supplier;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.eriklievaart.q.engine.convert.CollectionConvertersProvider;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QMainUi;
import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class OsgiSupplierFactory {

	private BundleContext context;
	private EngineSupplierFactory engineSupplierFactory;

	public OsgiSupplierFactory(BundleContext context) {
		this.context = context;
		engineSupplierFactory = new EngineSupplierFactory(getUrlResolverSupplier(), getQContextSupplier(), getMainUi());
	}

	public EngineSupplierFactory getEngineSupplierFactory() {
		return engineSupplierFactory;
	}

	private Supplier<QContext> getQContextSupplier() {
		return () -> getService(QMainUi.class).getQContext();
	}

	public Supplier<UrlResolver> getUrlResolverSupplier() {
		return () -> context.getService(context.getServiceReference(UrlResolver.class));
	}

	public Supplier<QMainUi> getMainUi() {
		return () -> getService(QMainUi.class);
	}

	public Supplier<Converters> getCollectionsConvertersSupplier() {
		return new CollectionConvertersProvider(getUrlResolverSupplier());
	}

	public <S> S getService(Class<S> type) {
		return context.getService(context.getServiceReference(type));
	}

	public <S> S getService(ServiceReference<S> reference) {
		return context.getService(reference);
	}

	public <T> List<T> getServices(Class<T> type) {
		try {
			List<T> plugins = NewCollection.list();
			for (ServiceReference<T> reference : context.getServiceReferences(type, null)) {
				plugins.add(getService(reference));
			}
			return plugins;

		} catch (InvalidSyntaxException e) {
			throw new RuntimeException("This should never happen", e);
		}
	}
}