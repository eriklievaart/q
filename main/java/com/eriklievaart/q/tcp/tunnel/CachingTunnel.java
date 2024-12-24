package com.eriklievaart.q.tcp.tunnel;

import com.eriklievaart.q.tcp.shared.TunnelVO;

public class CachingTunnel implements TcpTunnel {

	private TcpTunnel delegate;

	public CachingTunnel(TcpTunnel delegate) {
		this.delegate = delegate;
	}

	@Override
	public void sendVO(TunnelVO vo) {
		delegate.sendVO(vo);
	}

	@Override
	public TunnelVO receiveVO() {
		return delegate.receiveVO();
	}
}
