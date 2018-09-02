package com.eriklievaart.q.zdelete;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

class TrashCache {

	static final String TRASH_NAME = ".Trash-1000";
	static final String FILES_NAME = "files";

	private final Set<SystemFile> trashParents = new HashSet<>();
	private final Map<String, Boolean> unavailable = new WeakHashMap<>();

	Optional<SystemFile> getTrashLocation(final VirtualFile file) {
		if (!file.getUrl().getProtocol().equals("file")) {
			return Optional.empty(); // use Trash for local filesystem only, finding the trash might add too much overhead
		}
		for (VirtualFile trash : trashParents) {
			if (trash.isParentOf(file)) {
				return Optional.of(getTrashFolder((SystemFile) trash));
			}
		}
		Optional<? extends VirtualFile> optional = file.getParentFile();
		return optional.isPresent() ? lookup((SystemFile) optional.get()) : Optional.empty();
	}

	private Optional<SystemFile> lookup(final SystemFile file) {
		if (unavailable.containsKey(file.getPath())) {
			return Optional.empty();
		}
		VirtualFile trash = file.resolve(TRASH_NAME);
		if (trash.exists() && trash.isDirectory()) {
			SystemFile move = getTrashFolder(file);
			trashParents.add(move);
			return Optional.of(move);
		}
		unavailable.put(file.getPath(), true);
		Optional<SystemFile> optional = file.getParentFile();
		return optional.isPresent() ? lookup(optional.get()) : Optional.empty();
	}

	private SystemFile getTrashFolder(final SystemFile parent) {
		return parent.resolve(TRASH_NAME).resolve(FILES_NAME);
	}

}
