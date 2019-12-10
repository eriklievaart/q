package com.eriklievaart.q.ui.api;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;

/**
 * Snapshot of selected locations.
 */
public class QContext {

	private final BrowserContext left;
	private final BrowserContext right;
	private boolean leftActive;

	public QContext(BrowserContext left, BrowserContext right) {
		Check.notNull(left, right);
		this.left = left;
		this.right = right;
	}

	public void setLeftActive() {
		leftActive = true;
	}

	public void setRightActive() {
		leftActive = false;
	}

	public BrowserContext getActive() {
		return leftActive ? left : right;
	}

	public BrowserContext getLeft() {
		return left;
	}

	public BrowserContext getRight() {
		return right;
	}

	public boolean isLeftActive() {
		return leftActive;
	}

	public boolean isRightActive() {
		return !leftActive;
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$ <=> $]", left.getDirectory().getPath(), right.getDirectory().getPath());
	}
}