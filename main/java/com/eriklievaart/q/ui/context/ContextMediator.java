package com.eriklievaart.q.ui.context;

import java.util.List;
import java.util.stream.Collectors;

import com.eriklievaart.q.ui.api.BrowserContext;
import com.eriklievaart.q.ui.api.QContext;
import com.eriklievaart.q.ui.event.BrowserObserver;
import com.eriklievaart.q.ui.main.BrowserComponents;
import com.eriklievaart.q.ui.main.UiComponents;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

/**
 * Context object that registers the locations opened in the left and right file browser lists. Also keeps track of
 * which browser list is active (last selected).
 *
 * @author Erik Lievaart
 */
public class ContextMediator {
	private final LogTemplate log = new LogTemplate(getClass());

	private final LruIndex index;
	private final BrowserObserver leftObserver;
	private final BrowserObserver rightObserver;

	private BrowserOrientation active = BrowserOrientation.LEFT;
	private VirtualFile left;
	private VirtualFile right;
	private boolean showHiddenFiles = true;

	public ContextMediator(LruIndex index, BrowserObserver leftObserver, BrowserObserver rightObserver) {
		this.index = index;
		this.leftObserver = leftObserver;
		this.rightObserver = rightObserver;
	}

	/**
	 * Get the {@link UIComponents} for the active file browser list.
	 */
	public BrowserComponents getActiveBrowser(UiComponents components) {
		return active == BrowserOrientation.LEFT ? components.leftBrowser : components.rightBrowser;
	}

	/**
	 * Get the {@link UIComponents} for the active file browser list.
	 */
	public BrowserComponents getInactiveBrowser(UiComponents components) {
		return active == BrowserOrientation.LEFT ? components.rightBrowser : components.leftBrowser;
	}

	/**
	 * Get the {@link VirtualFile} for the active file browser list.
	 */
	public VirtualFile getActive() {
		return active == BrowserOrientation.LEFT ? left : right;
	}

	/**
	 * Get the {@link VirtualFile} for the inactive file browser list.
	 */
	public VirtualFile getInactive() {
		return active == BrowserOrientation.LEFT ? right : left;
	}

	/**
	 * Get the {@link VirtualFile} for the left file browser list.
	 */
	public VirtualFile getLeft() {
		return left;
	}

	/**
	 * Get the {@link VirtualFile} for the right file browser list.
	 */
	public VirtualFile getRight() {
		return right;
	}

	/**
	 * Mark the left file browser list as active.
	 */
	public void setLeftActive() {
		active = BrowserOrientation.LEFT;
	}

	/**
	 * Mark the right file browser list as active.
	 */
	public void setRightActive() {
		active = BrowserOrientation.RIGHT;
	}

	/**
	 * Is the right browser window active.
	 */
	public boolean isLeftActive() {
		return active == BrowserOrientation.LEFT;
	}

	public QContext getContext() {
		BrowserContext leftContext = new BrowserContext(left, leftObserver.getSelection());
		BrowserContext rightContext = new BrowserContext(right, rightObserver.getSelection());
		QContext context = new QContext(leftContext, rightContext);

		if (isLeftActive()) {
			context.setLeftActive();
		} else {
			setRightActive();
		}
		return context;
	}

	/**
	 * : Swap the contents of the left and right file browser lists.
	 */
	public void swap() {
		VirtualFile oldLeft = getLeft();
		VirtualFile oldRight = getRight();

		setLocation(BrowserOrientation.LEFT, oldRight);
		setLocation(BrowserOrientation.RIGHT, oldLeft);
	}

	public void mirror() {
		setLocation(BrowserOrientation.INACTIVE, getActive());
	}

	public void setLocation(final BrowserOrientation orientation, final VirtualFile dir) {
		BrowserOrientation simple = simplify(orientation);
		if (simple == BrowserOrientation.LEFT) {
			left = dir;
			leftObserver.update(dir);
		} else {
			right = dir;
			rightObserver.update(dir);
		}
		index.add(dir.getUrl().getUrlUnescaped());
	}

	public VirtualFile getSelectedDirectory(BrowserOrientation orientation) {
		return simplify(orientation) == BrowserOrientation.LEFT ? getLeft() : getRight();
	}

	private BrowserOrientation simplify(BrowserOrientation orientation) {
		boolean leftActive = active == BrowserOrientation.LEFT && orientation == BrowserOrientation.ACTIVE;
		boolean leftInactive = active == BrowserOrientation.RIGHT && orientation == BrowserOrientation.INACTIVE;
		if (orientation == BrowserOrientation.LEFT || leftActive || leftInactive) {
			return BrowserOrientation.LEFT;
		}
		return BrowserOrientation.RIGHT;
	}

	public BrowserComponents getBrowser(BrowserOrientation orientation, UiComponents components) {
		BrowserOrientation simplified = simplify(orientation);
		return simplified == BrowserOrientation.LEFT ? components.leftBrowser : components.rightBrowser;
	}

	public void setActive(BrowserOrientation orientation) {
		if (orientation == BrowserOrientation.LEFT) {
			setLeftActive();
		}
		if (orientation == BrowserOrientation.RIGHT) {
			setRightActive();
		}
		if (orientation == BrowserOrientation.INACTIVE) {
			active = active == BrowserOrientation.LEFT ? BrowserOrientation.RIGHT : BrowserOrientation.LEFT;
		}
	}

	public void shutdown() {
		leftObserver.shutdown();
		rightObserver.shutdown();
	}

	public void toggleHidden() {
		showHiddenFiles = !showHiddenFiles;
		log.info("Show hidden files: $", showHiddenFiles);
		leftObserver.showHiddenFiles(showHiddenFiles);
		rightObserver.showHiddenFiles(showHiddenFiles);
	}

	public List<String> getActiveUrlsAsStrings() {
		BrowserContext context = isLeftActive() ? getContext().getLeft() : getContext().getRight();
		return context.getUrls().stream().map(f -> f.getUrl().getUrlEscaped()).collect(Collectors.toList());
	}

}
