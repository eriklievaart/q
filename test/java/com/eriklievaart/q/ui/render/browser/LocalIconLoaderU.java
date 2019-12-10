package com.eriklievaart.q.ui.render.browser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.plaf.metal.MetalIconFactory;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.MapTool;
import com.eriklievaart.toolkit.lang.api.collection.MultiMap;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.collection.SetTool;

public class LocalIconLoaderU {

	@Test
	public void mapExtToIconsSingle() {

		Map<String, Icon> icons = MapTool.of("audio/mpeg", MetalIconFactory.getTreeLeafIcon());
		MultiMap<String, String> extToFile = NewCollection.multiMap();
		extToFile.add("mp3", "audio/mpeg");
		Map<String, Icon> extToIcons = LocalIconLoader.mapExtToIcons(extToFile, icons);

		Check.notNull(extToIcons.get("mp3"));
		CheckCollection.isSize(extToIcons, 1);
	}

	@Test
	public void mapExtToIconsMultiple() {

		Map<String, Icon> icons = MapTool.of("audio/mpeg", MetalIconFactory.getTreeLeafIcon());
		MultiMap<String, String> extToFile = NewCollection.multiMap();
		extToFile.add("mp3", "audio/mpeg");
		extToFile.add("mpeg", "audio/mpeg");
		Map<String, Icon> extToIcons = LocalIconLoader.mapExtToIcons(extToFile, icons);

		Check.notNull(extToIcons.get("mp3"));
		Check.notNull(extToIcons.get("mpeg"));
		CheckCollection.isSize(extToIcons, 2);
	}

	@Test
	public void getMimeToFileAlternativesSingle() {
		Map<String, String> other = MapTool.of("audio/mpeg", " audio/mp4-vlc ");
		Set<String> mimes = SetTool.of("audio/mpeg");
		MultiMap<String, String> mimeToFile = LocalIconLoader.getMimeToFiles(mimes, other);

		Iterator<String> iter = mimeToFile.get("audio/mpeg").iterator();
		Check.isEqual(iter.next(), "audio-mpeg");
		Check.isEqual(iter.next(), "audio-mp4-vlc");
		Check.isFalse(iter.hasNext());
	}

	@Test
	public void getMimeToFileAlternativesWildcard() {
		Map<String, String> other = MapTool.of("audio/*", "audio/mp4-vlc");
		Set<String> mimes = SetTool.of("audio/mpeg");
		MultiMap<String, String> mimeToFile = LocalIconLoader.getMimeToFiles(mimes, other);

		Iterator<String> iter = mimeToFile.get("audio/mpeg").iterator();
		Check.isEqual(iter.next(), "audio-mpeg");
		Check.isEqual(iter.next(), "audio-mp4-vlc");
		Check.isFalse(iter.hasNext());
	}

	@Test
	public void createExtMapping() {
		MultiMap<String, String> mimeToFile = NewCollection.multiMap();
		mimeToFile.add("audio/mpeg", "audio-mpeg");
		mimeToFile.add("audio/mpeg", "audio-mp4-vlc");
		Map<String, String> ini = MapTool.of("audio/mpeg", " mp3 mpeg ");
		MultiMap<String, String> extToFiles = LocalIconLoader.createExtMapping(ini, mimeToFile);

		Check.isEqual(extToFiles.get("mp3"), Arrays.asList("audio-mpeg", "audio-mp4-vlc"));
		Check.isEqual(extToFiles.get("mpeg"), Arrays.asList("audio-mpeg", "audio-mp4-vlc"));
		Check.isTrue(extToFiles.size() == 2);
	}
}