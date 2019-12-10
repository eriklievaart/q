package com.eriklievaart.q.ui.render.browser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.eriklievaart.q.ui.config.UiResourcePaths;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckStr;
import com.eriklievaart.toolkit.lang.api.collection.MultiMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.pattern.PatternTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.vfs.api.VirtualFileScanner;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

/**
 * @author Erik Lievaart
 *
 *         Jim: Mike, why do you need this class? Well Jim, coincidentally, I was just asking myself the same question.
 *         You see JAVA provides the FileSystemView class. Jim: uhuh. Mike: This class provides a COMPLETELY os
 *         independent view of the file system and its icons, but there is only one fallacy. Jim: What's that Mike? It
 *         only works under Windows. Gee Mike, that's a b****! Mike: Tone it down Jim, this Javadoc is publicly
 *         available. Jim: Oh right, right. Mike: So anywayz, I googled a bit looking for a platform independent
 *         solution which doesn't seem to exist, not even a distribution independent solution exists for Linux, but
 *         there is a nice world of obscure C code out there. So I started looking for Icon sets and I found the Open
 *         Icon set or sumthin. This Icon set contains icons for a collection of MIME types and it has directories of
 *         icons that are named after and closely resemble Linux window managers' icons. They also have a listing of
 *         file extensions and their mime types, so I parsed the HTML table to get the results. Jim: Wow! Mike, that's
 *         really clever. Mike: Yes, Jim that's what I thought too, that's EXACTLY what I was thinking. However, the
 *         names of the MIME types in the icon sets don't match with each other, so I created a translation file that
 *         maps MIME types in the extension file to mime types in the icon set. Jim: Hold on for a bit, you're mapping
 *         MIME types to MIME types? Mike: Yup! Jim: but isn't that the kind of problem we want to solve by using MIME
 *         types? Mike: yes, that's EXACTLY the sort of thing we would normally want to solve by using MIME types, but I
 *         don't want to rename the icons, because that would make upgrading difficult if new icons are added to the
 *         Icon set. I don't want to change the parsed HTML file, because I would lose information and there's also the
 *         problems when the Icon set gets larger and files have to be remapped, so I created a mapping between the MIME
 *         types and the MIME types. So I found myself an ugly hack to solve a problem that shouldn't even exist,
 *         because of the 'grand' FileSystemView class. Jim: aaahh, so that's why you need this class! This was so
 *         educational! Oh, and mind you, its possible to create generic fallbacks with a wildcard for a collection of
 *         unnamed MIME types.
 */
public class LocalIconLoader {

	private final LogTemplate log = new LogTemplate(getClass());
	private final Map<String, Icon> mapping = NewCollection.map();
	private Icon dir;

	public LocalIconLoader(UiResourcePaths resources) {
		try {
			load(resources);
		} catch (RuntimeIOException e) {
			log.warn("unable to load icons: $", e, e.getMessage());
		}
	}

	private void load(UiResourcePaths resources) {
		Map<String, String> ini = PropertiesIO.loadStrings(resources.getMimeTypes());
		Map<String, String> alternatives = PropertiesIO.loadStrings(resources.getMimeFallbacks());

		MultiMap<String, String> mimeToFile = getMimeToFiles(ini.keySet(), alternatives);
		MultiMap<String, String> extToFile = createExtMapping(ini, mimeToFile);

		mapping.putAll(mapExtToIcons(extToFile, loadIcons(resources)));
		log.info("% icons found for % mime types", mapping.size(), ini.size());
	}

	Icon getDirIcon() {
		return dir;
	}

	Map<String, Icon> getMapping() {
		return mapping;
	}

	static Map<String, Icon> mapExtToIcons(final MultiMap<String, String> extToFile, final Map<String, Icon> icons) {
		Map<String, Icon> extToIcon = NewCollection.map();
		for (String ext : extToFile.keySet()) {
			for (String file : extToFile.get(ext)) {
				if (icons.containsKey(file)) {
					extToIcon.put(ext, icons.get(file));
					break;
				}
			}
			Check.isTrue(extToIcon.containsKey(ext), "No Icon found for ext %: %", ext, extToFile.get(ext));
		}
		return extToIcon;
	}

	static MultiMap<String, String> getMimeToFiles(final Set<String> mimes, final Map<String, String> fallback) {
		MultiMap<String, String> mimeToFile = NewCollection.multiMap();
		for (String mime : mimes) {
			mimeToFile.add(mime, replaceSlashes(mime));

			List<String> alternatives = NewCollection.list();
			if (fallback.get(mime) != null) {
				alternatives.addAll(words(replaceSlashes(fallback.get(mime))));
			}
			if (fallback.containsKey(genericMime(mime))) {
				alternatives.addAll(words(replaceSlashes(fallback.get(genericMime(mime)))));
			}
			mimeToFile.addAll(mime, alternatives);
		}
		return mimeToFile;
	}

	static MultiMap<String, String> createExtMapping(final Map<String, String> ini,
			final MultiMap<String, String> mimeToFile) {

		MultiMap<String, String> extToFile = NewCollection.multiMap();
		for (String mime : ini.keySet()) {
			CheckStr.notEmpty(mime);
			Check.isTrue(ini.containsKey(mime), "Missing icon for mime type %", mime);

			for (String ext : words(ini.get(mime))) {
				extToFile.addAll(ext, mimeToFile.get(mime));
			}
		}
		return extToFile;
	}

	private static List<String> words(final String input) {
		return input == null ? null : PatternTool.findAll("\\S++", input);
	}

	private static String replaceSlashes(final String mime) {
		return mime == null ? null : PatternTool.replaceAll("/", "-", mime);
	}

	private static String genericMime(final String mime) {
		return PatternTool.getGroup1("([^/]++/).*+", mime) + '*';
	}

	private Map<String, Icon> loadIcons(UiResourcePaths resources) {
		try {
			dir = loadIcon(resources.getIconFile("folder.png"));

			Map<String, Icon> icons = NewCollection.map();
			VirtualFileScanner scanner = new VirtualFileScanner(new SystemFile(resources.getIconDirectory()));
			for (VirtualFile icon : scanner) {
				boolean ext = icon.getUrl().getExtension().toLowerCase().equals("png");
				Check.isTrue(ext, "Only `.png` files allowed in icon dir, found $", icon);
				log.trace("Reading image $", icon);
				SystemFile sf = (SystemFile) icon;
				icons.put(icon.getUrl().getBaseName(), loadIcon(sf.unwrap()));
			}
			return icons;

		} catch (Exception e) {
			throw new RuntimeIOException("Icon dir error % %", e, resources.getIconDirectory(), e.getMessage());
		}
	}

	private ImageIcon loadIcon(final File file) throws IOException {
		log.trace("loading icon: $", file);
		return new ImageIcon(ImageIO.read(file));
	}
}