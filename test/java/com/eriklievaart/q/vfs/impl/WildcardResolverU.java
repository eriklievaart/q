package com.eriklievaart.q.vfs.impl;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.SetTool;

public class WildcardResolverU {

	// ////////////// //
	// case sensitive //
	// ////////////// //

	@Test
	public void resolvePartialEmpty() {
		String match = new WildcardResolver("", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isEqual(match, "media");
	}

	@Test
	public void resolvePartial() {
		String match = new WildcardResolver("mn", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isEqual(match, "mnt");
	}

	@Test
	public void resolvePartialCase() {
		String match = new WildcardResolver("X", SetTool.treeSet("xkb", "Xreset.d", "Xresources")).wildcardMatch();
		Check.isEqual(match, "Xreset.d");
	}

	@Test
	public void resolvePartialNone() {
		String match = new WildcardResolver("M", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isNull(match);
	}

	@Test
	public void resolvePartialExact() {
		String match = new WildcardResolver("opt", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isEqual(match, "opt");
	}

	@Test
	public void resolveWildcardSingle() {
		String match = new WildcardResolver("o?t", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isEqual(match, "opt");
	}

	@Test
	public void resolveWildcardMultiple() {
		String match = new WildcardResolver("m*d*a", SetTool.treeSet("media", "mnt", "opt")).wildcardMatch();
		Check.isEqual(match, "media");
	}

	// //////////////// //
	// case insensitive //
	// //////////////// //

	public static void main(final String[] args) {
		new WildcardResolverU().resolveInsensitiveEmpty();
	}

	@Test
	public void resolveInsensitiveEmpty() {
		String match = new WildcardResolver("", SetTool.treeSet("Media", "mnt", "opt")).wildcardInsensitiveMatch();
		Check.isEqual(match, "Media");
	}

	@Test
	public void resolveInsensitiveCaseDifference() {
		String match = new WildcardResolver("s", SetTool.treeSet("situation", "Don't Go")).wildcardInsensitiveMatch();
		Check.isEqual(match, "situation");
	}

	@Test
	public void resolveInsensitiveCaseMatch() {
		String match = new WildcardResolver("s", SetTool.treeSet("Situation", "Don't Go")).wildcardInsensitiveMatch();
		Check.isEqual(match, "Situation");
	}

	@Test
	public void resolvePartialInsensitiveNone() {
		String match = new WildcardResolver("M", SetTool.treeSet("omg", "it's", "dead")).wildcardInsensitiveMatch();
		Check.isNull(match);
	}

	@Test
	public void resolvePartialInsensitiveExact() {
		String match = new WildcardResolver("OPT", SetTool.treeSet("media", "mnt", "opt")).wildcardInsensitiveMatch();
		Check.isEqual(match, "opt");
	}

	@Test
	public void resolveWildcardInsensitiveSingle() {
		String match = new WildcardResolver("o?t", SetTool.treeSet("media", "mnt", "opt")).wildcardInsensitiveMatch();
		Check.isEqual(match, "opt");
	}

	@Test
	public void resolveWildcardInsensitiveMultiple() {
		String match = new WildcardResolver("m*D*a", SetTool.treeSet("media", "opt")).wildcardInsensitiveMatch();
		Check.isEqual(match, "media");
	}

	// ///////////// //
	// resolve fuzzy //
	// ///////////// //

	@Test
	public void resolveFuzzyPartial() {
		String match = new WildcardResolver("X", SetTool.treeSet("xkb", "Xreset.d", "Xresources")).resolve();
		Check.isEqual(match, "Xreset.d");
	}

	@Test
	public void resolveFuzzyPartialInsensitive() {
		String match = new WildcardResolver("m", SetTool.treeSet("Softly Over", "Mister Blue")).resolve();
		Check.isEqual(match, "Mister Blue");
	}
}