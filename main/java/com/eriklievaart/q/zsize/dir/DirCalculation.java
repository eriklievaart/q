package com.eriklievaart.q.zsize.dir;

import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class DirCalculation implements Comparable<DirCalculation> {

	private VirtualFile file;
	private long size;

	public DirCalculation(VirtualFile file, long size) {
		this.file = file;
		this.size = size;
	}

	public long getSize() {
		return size;
	}

	public String getName() {
		return file.getName();
	}

	@Override
	public int compareTo(DirCalculation o) {
		long diff = size - o.size;
		if (diff < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		if (diff > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return 0;
	}
}