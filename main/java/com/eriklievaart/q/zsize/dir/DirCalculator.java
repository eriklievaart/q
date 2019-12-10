package com.eriklievaart.q.zsize.dir;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class DirCalculator {

	public long sum(VirtualFile file) {
		if (Thread.interrupted()) {
			throw new RuntimeException("Thread interrupted!");
		}
		try {
			if (file.isFile()) {
				return file.length();
			}
			if (file.isDirectory()) {
				long sum = 0;
				for (VirtualFile child : file.getChildren()) {
					sum += sum(child);
				}
				return sum;
			}
		} catch (RuntimeIOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}