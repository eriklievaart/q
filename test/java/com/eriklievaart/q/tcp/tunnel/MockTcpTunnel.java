package com.eriklievaart.q.tcp.tunnel;

import java.util.List;

import com.eriklievaart.q.tcp.shared.TunnelVO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class MockTcpTunnel implements TcpTunnel {

	private boolean linked = false;
	private List<TunnelVO> in = NewCollection.list();
	private List<TunnelVO> out = NewCollection.list();

	@Override
	public void sendVO(TunnelVO vo) {
		out.add(vo);
	}

	@Override
	public TunnelVO receiveVO() {
		CheckCollection.notEmpty(in);
		return in.remove(0);
	}

	public TunnelVO popSent() {
		return out.remove(0);
	}

	public void link(MockTcpTunnel partner) {
		Check.isFalse(linked);
		linked = true;

		partner.in = out;
		partner.out = in;
		partner.linked = true;
	}
}
