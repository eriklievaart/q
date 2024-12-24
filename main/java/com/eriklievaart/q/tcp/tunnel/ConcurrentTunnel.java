package com.eriklievaart.q.tcp.tunnel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.eriklievaart.q.tcp.shared.TunnelVO;

public class ConcurrentTunnel implements TcpTunnel {

	private TcpTunnel delegate;
	private Lock mutex = new ReentrantLock();

	public ConcurrentTunnel(TcpTunnel delegate) {
		this.delegate = delegate;
	}

	@Override
	public void sendVO(TunnelVO vo) {
		mutex.lock();
		try {
			delegate.sendVO(vo);

		} finally {
			mutex.unlock();
		}
	}

	@Override
	public TunnelVO receiveVO() {
		mutex.lock();
		try {
			return delegate.receiveVO();

		} finally {
			mutex.unlock();
		}
	}

	@Override
	public TunnelVO sendAndReceiveVO(TunnelVO vo) {
		mutex.lock();
		try {
			delegate.sendVO(vo);
			return delegate.receiveVO();

		} finally {
			mutex.unlock();
		}
	}

	public void tryLock(Consumer<TcpTunnel> consumer) {
		if (mutex.tryLock()) {
			try {
				consumer.accept(delegate);
			} finally {
				mutex.unlock();
			}
		}
	}

	public Lock getLock() {
		return mutex;
	}

	public void block(Runnable run) {
		mutex.lock();
		try {
			run.run();
		} finally {
			mutex.unlock();
		}
	}
}
