package com.eriklievaart.q.zfind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class FindIterator implements Iterator<VirtualFile> {
	private LogTemplate log = new LogTemplate(getClass());

	private final List<VirtualFile> queue;
	private final List<Predicate<VirtualFile>> checks;
	private VirtualFile next;
	private boolean local = false;

	private FindIterator(VirtualFile root, List<Predicate<VirtualFile>> checks, boolean local) {
		this.checks = checks;
		this.local = local;
		this.queue = root.isDirectory() ? getChildrenSorted(root) : NewCollection.list();
		findNext();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<VirtualFile> getChildrenSorted(VirtualFile root) {
		List<VirtualFile> children = new ArrayList(root.getChildren());
		Collections.sort(children, (a, b) -> a.getName().toUpperCase().compareTo(b.getName().toUpperCase()));
		return children;
	}

	public static FindIterator local(VirtualFile root, List<Predicate<VirtualFile>> checks) {
		return new FindIterator(root, checks, true);
	}

	public static FindIterator recursive(VirtualFile root, List<Predicate<VirtualFile>> checks) {
		return new FindIterator(root, checks, false);
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public VirtualFile next() {
		VirtualFile current = next;
		findNext();
		return current;
	}

	private void findNext() {
		while (queue.size() > 0) {
			if (Thread.interrupted()) {
				throw new FormattedException("Thread interrupted");
			}
			VirtualFile traverse = queue.remove(0);
			log.trace("traversing $", traverse);
			if (traverse.isDirectory() && !local) {
				queueChildren(traverse);
			}
			if (isValid(traverse)) {
				log.debug("valid result: $", traverse);
				next = traverse;
				return;
			}
		}
		next = null;
	}

	private void queueChildren(VirtualFile traverse) {
		try {
			queue.addAll(0, getChildrenSorted(traverse));
		} catch (RuntimeIOException e) {
			log.debug("unable to access $", traverse.getPath());
		}
	}

	private boolean isValid(VirtualFile traverse) {
		for (Predicate<VirtualFile> predicate : checks) {
			if (!predicate.test(traverse)) {
				return false;
			}
		}
		return true;
	}

	public void setLocal(boolean value) {
		local = value;
	}
}
