package com.eriklievaart.q.doc;

import java.io.File;

import com.eriklievaart.q.doc.generate.DocGenerator;
import com.eriklievaart.toolkit.io.api.FileTool;

public class DocEclipse {

	public static void main(String[] args) {
		File git = new File("/home/erikl/Development/git/q");
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