package com.eriklievaart.q.engine.impl;

import org.junit.Test;

import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.api.QContextFactory;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class VariableResolverU {

	@Test
	public void resolveLeftDir() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().leftDir("/pineapple/boy").make();
		String result = resolver.lookup("dir1", context);
		Check.isEqual(result, "mem:///pineapple/boy");
	}

	@Test
	public void resolveRightDir() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().rightDir("/peach/boy").make();
		String result = resolver.lookup("dir2", context);
		Check.isEqual(result, "mem:///peach/boy");
	}

	@Test
	public void resolveActiveDirLeft() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().leftDir("/banana/boy").make();
		context.setLeftActive();
		String result = resolver.lookup("dir", context);
		Check.isEqual(result, "mem:///banana/boy");
	}

	@Test
	public void resolveActiveDirRight() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().rightDir("/grapefruit/boy").make();
		context.setRightActive();
		String result = resolver.lookup("dir", context);
		Check.isEqual(result, "mem:///grapefruit/boy");
	}

	@Test
	public void resolveInactiveDirLeft() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().leftDir("/cherry/boy").make();
		context.setRightActive();
		String result = resolver.lookup("dir~", context);
		Check.isEqual(result, "mem:///cherry/boy");
	}

	@Test
	public void resolveDirName() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().leftDir("/chestnut/boy").make();
		String result = resolver.lookup("dirname1", context);
		Check.isEqual(result, "boy");
	}

	@Test
	public void resolveParent() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().leftDir("/cucumber/boy").make();
		String result = resolver.lookup("parent", context);
		Check.isEqual(result, "mem:///cucumber");
	}

	@Test
	public void resolveUrlsEmpty() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().make();
		String result = resolver.lookup("urls", context);
		Check.isEqual(result, "");
	}

	@Test
	public void resolveUrl() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().urlLeft("apple").make();
		String result = resolver.lookup("url", context);
		Check.isEqual(result, "mem:///apple");
	}

	@Test
	public void resolveUrls() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().urlLeft("eggplant").make();
		String result = resolver.lookup("urls", context);
		Check.isEqual(result, "mem:///eggplant");
	}

	@Test
	public void resolveName() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().urlLeft("carrot").make();
		String result = resolver.lookup("urlname", context);
		Check.isEqual(result, "carrot");
	}

	@Test
	public void resolveNames() throws Exception {
		VariableResolver resolver = new VariableResolver();
		QContext context = new QContextFactory().urlLeft("broccoli").make();
		String result = resolver.lookup("urlnames", context);
		Check.isEqual(result, "broccoli");
	}
}