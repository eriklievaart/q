package com.eriklievaart.q.tcp.shared;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;

public class ChannelVOU {

	@Test
	public void getBodyAsLines() {
		TunnelVO vo = new TunnelVO(TunnelCommand.RESPONSE);
		Check.isEqual(vo.getBodyAsLines(), new String[0]);

		vo.setBody(Arrays.asList("one", "two"));
		Assertions.assertThat(vo.getBodyAsLines()).containsExactly("one", "two");
	}
}
