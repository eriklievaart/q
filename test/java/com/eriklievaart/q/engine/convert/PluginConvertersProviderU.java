package com.eriklievaart.q.engine.convert;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.eriklievaart.q.vfs.api.UrlResolver;
import com.eriklievaart.q.vfs.impl.UrlResolverService;
import com.eriklievaart.toolkit.convert.api.Converters;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@SuppressWarnings("unchecked")
public class PluginConvertersProviderU {

	@Test
	public void convertBasic() throws Exception {
		Check.isEqual(createConverters().to(String.class, "foobar"), "foobar");
	}

	@Test
	public void convertList() throws Exception {
		Assertions.assertThat(createConverters().to(List.class, "1 2")).containsExactly("1", "2");
	}

	@Test
	public void convertMap() throws Exception {
		Map<String, String> map = createConverters().to(Map.class, "a=b");
		Assertions.assertThat(map).containsKey("a");
	}

	@Test
	public void convertUri() throws Exception {
		URI url = createConverters().to(URI.class, "http://example.com");
		Check.isEqual("example.com", url.getHost());
	}

	@Test
	public void convertVirtualFile() throws Exception {
		VirtualFile file = createConverters().to(VirtualFile.class, "mem:///bla");
		Check.isEqual(file.getPath(), "/bla");
	}

	private Converters createConverters() {
		UrlResolver resolver = new UrlResolverService();
		return new CollectionConvertersProvider(() -> resolver).get();
	}
}
