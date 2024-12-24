package com.eriklievaart.q.tcp.tunnel;

import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class LoggingTunnel implements TcpTunnel {
	private LogTemplate log = new LogTemplate(getClass());

	private TcpTunnel delegate;

	public LoggingTunnel(TcpTunnel delegate) {
		this.delegate = delegate;
	}

	@Override
	public void sendVO(TunnelVO vo) {
		log.debug("sending: $", vo);
		delegate.sendVO(vo);
	}

	@Override
	public TunnelVO receiveVO() {
		TunnelVO vo = delegate.receiveVO();
		log.debug("received: $", vo);
		return vo;
	}
}
