package com.eriklievaart.q.zsize;

import java.util.List;

import com.eriklievaart.q.api.engine.Invokable;
import com.eriklievaart.q.api.engine.PluginContext;
import com.eriklievaart.q.api.engine.PluginException;
import com.eriklievaart.q.api.engine.annotation.Doc;
import com.eriklievaart.q.api.engine.annotation.Flag;
import com.eriklievaart.q.zsize.dir.DirCalculation;
import com.eriklievaart.q.zsize.dir.DirCalculator;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

@Doc("determine sizes of subdirectories")
class SizeShellCommand implements Invokable {

	private SizeController controller;
	private VirtualFile dir;

	public SizeShellCommand(SizeController controller) {
		this.controller = controller;
	}

	@Flag(group = "main", values = { "$dir" }, primary = true)
	@Doc("Calculate dir sizes. One argumen: root dir")
	public SizeShellCommand directory(final VirtualFile parent) {
		this.dir = parent;
		return this;
	}

	@Override
	public void invoke(PluginContext context) throws Exception {
		DirCalculator calculator = new DirCalculator();
		List<DirCalculation> sizes = NewCollection.list();

		controller.init();
		for (VirtualFile child : dir.getChildrenAdvanced().getAlphabeticallyDirectoriesFirst()) {
			DirCalculation calculation = new DirCalculation(child, calculator.sum(child));
			insert(calculation, sizes);
			controller.showLabel(dir, sizes);
			controller.showResults(sizes);
		}
	}

	private void insert(DirCalculation calculation, List<DirCalculation> sizes) {
		int insert = 0;
		while (insert < sizes.size() && calculation.getSize() < sizes.get(insert).getSize()) {
			insert++;
		}
		sizes.add(insert, calculation);
	}

	@Override
	public void validate(PluginContext context) throws PluginException {
		Check.isTrue(dir.isDirectory(), "Not a directory %", dir);
	}
}