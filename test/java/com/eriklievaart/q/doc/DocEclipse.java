package com.eriklievaart.q.doc;

import java.io.File;

import com.eriklievaart.q.doc.generate.DocGenerator;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.UrlTool;

public class DocEclipse {

	public static void main(String[] args) {
		String home = System.getProperty("user.home");
		File git = new File(UrlTool.append(home, "Development/git/q"));
		File sources = new File(git, "main/java");
		File output = new File("/tmp/q/doc");

		DocGenerator generator = new DocGenerator();
		generator.setSourceDir(sources);
		generator.setOutputDir(output);
		generator.invoke();
		FileTool.copyFile(new File(git, "main/static/zip/doc/style.css"), output);
		System.exit(0);
	}
}