package com.eriklievaart.q.api.render;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

/**
 * Wraps a VirtualFile and gives it a toString method that can be shown in the file browser windows.
 *
 * @author Erik Lievaart
 */
public class VirtualFileWrapper {

	private final VirtualFile file;

	/**
	 * Wrap a VirtualFile.
	 */
	public VirtualFileWrapper(final VirtualFile file) {
		Check.notNull(file);

		this.file = file;
	}

	/**
	 * Get the contained {@link VirtualFile}.
	 */
	public VirtualFile getVirtualFile() {
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VirtualFileWrapper) {
			VirtualFileWrapper other = (VirtualFileWrapper) obj;
			return file.equals(other.getVirtualFile());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return file.getName();
	}
}