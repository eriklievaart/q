package com.eriklievaart.q.ui.render.icons;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class Favicon {

	public Image getFavicon() {
		try {
			URL resource = getClass().getResource(UiResourcePaths.FAVICON);
			Check.notNull(resource, "favicon not found: %", UiResourcePaths.FAVICON);
			return ImageIO.read(resource);

		} catch (IOException e) {
			return null;
		}
	}
}
