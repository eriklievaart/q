package com.eriklievaart.q.tcp.shared;

import java.io.IOException;

import org.junit.Test;

import com.eriklievaart.q.tcp.shared.chunk.ChunkIterator;
import com.eriklievaart.toolkit.io.api.StreamTool;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ChunkIteratorU {

	@Test
	public void iterate() throws IOException {
		ChunkIterator testable = new ChunkIterator(StreamTool.toInputStream("hello world!"));

		Check.isTrue(testable.hasNext());
		Check.isEqual(testable.nextString(), "hello world!");
		Check.isFalse(testable.hasNext());
	}
}
