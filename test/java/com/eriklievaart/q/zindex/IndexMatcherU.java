package com.eriklievaart.q.zindex;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class IndexMatcherU {

	@Test
	public void lookupExact() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/exact.txt");

		List<String> result = new IndexMatcher(urls).lookup("exact.txt");
		Assertions.assertThat(result).containsExactly("file:///tmp/exact.txt");
	}

	@Test
	public void lookupExactInsensitive() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/exact.txt");

		List<String> result = new IndexMatcher(urls).lookup("EXACT.txt");
		Assertions.assertThat(result).containsExactly("file:///tmp/exact.txt");

	}

	@Test
	public void lookupStarting() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/starting.txt");

		List<String> result = new IndexMatcher(urls).lookup("start");
		Assertions.assertThat(result).containsExactly("file:///tmp/starting.txt");

	}

	@Test
	public void lookupStartingInsensitive() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/starting.txt");

		List<String> result = new IndexMatcher(urls).lookup("START");
		Assertions.assertThat(result).containsExactly("file:///tmp/starting.txt");
	}

	@Test
	public void lookupContaining() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/containing.txt");

		List<String> result = new IndexMatcher(urls).lookup("ont");
		Assertions.assertThat(result).containsExactly("file:///tmp/containing.txt");

	}

	@Test
	public void lookupContainingInsensitive() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/containing.txt");

		List<String> result = new IndexMatcher(urls).lookup("ONT");
		Assertions.assertThat(result).containsExactly("file:///tmp/containing.txt");

	}

	@Test
	public void lookupPath() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/containing.txt");

		List<String> result = new IndexMatcher(urls).lookup("tmp tain");
		Assertions.assertThat(result).containsExactly("file:///tmp/containing.txt");
	}

	@Test
	public void lookupSortShortestPathFirst() {
		String l1 = "mem:///tmp/find";
		String l2 = "mem:///tmp/find/deeply";
		String l3 = "mem:///tmp/find/deeply/nested";
		String l4 = "mem:///tmp/find/deeply/nested/path";

		List<String> urls = ListTool.of(l4, l2, l1, l3);
		List<String> result = new IndexMatcher(urls).lookup("tmp find");
		Assertions.assertThat(result).containsExactly(l1, l2, l3, l4);
	}

	@Test
	public void lookupMixed() {
		List<String> urls = NewCollection.list();
		urls.add("file:///tmp/dummy.txt");
		urls.add("file:///tmp/exact.txt");
		urls.add("file:///tmp/CONTAINING.txt");
		urls.add("file:///tmp/ing.txt");
		urls.add("file:///tmp/ing");

		List<String> result = new IndexMatcher(urls).lookup("ing");
		String[] expect = new String[] { "file:///tmp/ing", "file:///tmp/ing.txt", "file:///tmp/CONTAINING.txt" };
		Assertions.assertThat(result).containsExactly(expect);
	}
}