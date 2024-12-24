package com.eriklievaart.q.tcp.tunnel;

import com.eriklievaart.q.tcp.shared.TunnelVO;

public interface TcpTunnel {

	public void sendVO(TunnelVO vo);

	public TunnelVO receiveVO();

	public default TunnelVO sendAndReceiveVO(TunnelVO vo) {
		sendVO(vo);
		return receiveVO();
	}
}
