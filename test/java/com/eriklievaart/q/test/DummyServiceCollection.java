package com.eriklievaart.q.test;

import java.util.function.Consumer;
import java.util.function.Function;

import com.eriklievaart.osgi.toolkit.api.ServiceCollection;

public class DummyServiceCollection<E> implements ServiceCollection<E> {

	@Override
	public <T> T anyReturns(Function<E, T> consumer) {
		return null;
	}

	@Override
	public <T> T oneReturns(Function<E, T> function) {
		return null;
	}

	@Override
	public void oneCall(Consumer<E> consumer) {
	}

	@Override
	public void allCall(Consumer<E> consumer) {
	}
}
