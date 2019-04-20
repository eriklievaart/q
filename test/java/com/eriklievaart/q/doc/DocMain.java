package com.eriklievaart.q.doc;

import java.io.File;

import com.eriklievaart.q.doc.generate.DocGenerator;
import com.eriklievaart.toolkit.ant.api.AntProperties;

public class DocMain {

	public static void main(String[] args) {
		File sources = AntProperties.getJavaSourceDir().getDirectory();
		File output = new File(AntProperties.getSpoolDir().getDirectory(), "zip/doc");

		DocGenerator generator = new DocGenerator();
		generator.setOutputDir(output);
		generator.setSourceDir(sources);
		generator.invoke();
	}
}
