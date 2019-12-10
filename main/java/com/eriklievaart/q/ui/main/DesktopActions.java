package com.eriklievaart.q.ui.main;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class DesktopActions {

	public static void open(VirtualFile file) {
		try {
			if (file instanceof SystemFile) {
				SystemFile sf = (SystemFile) file;
				Desktop.getDesktop().open(sf.unwrap());

			} else {
				Desktop.getDesktop().browse(new URI(file.getUrl().getUrlEscaped()));
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeIOException(e);
		}
	}
}