package com.eriklievaart.q.ui.main;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.api.Dialogs;
import com.eriklievaart.q.ui.context.BrowserOrientation;
import com.eriklievaart.q.ui.context.ContextMediator;
import com.eriklievaart.q.ui.event.EngineEvent;
import com.eriklievaart.q.ui.render.browser.VirtualFileWrapper;
import com.eriklievaart.q.ui.render.label.FileSize;
import com.eriklievaart.q.ui.render.label.LabelStyler;
import com.eriklievaart.toolkit.io.api.UrlTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.list.JListSelection;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class BrowserActions {
	private LogTemplate log = new LogTemplate(getClass());

	private UiBeanFactory beans;
	private EngineEvent engine;
	private ContextMediator mediator;
	private UiComponents components;
	private Dialogs dialogs;

	public BrowserActions(UiBeanFactory beans) {
		this.beans = beans;
		this.engine = beans.getEngineEvent();
		this.mediator = beans.getContextMediator();
		this.components = beans.getComponents();
		this.dialogs = beans.getDialogs();
	}

	public void putActions(Map<String, Consumer<ActionContext>> map) {
		addFileActions(map);
		addLocationActions(map);
		addBrowserActions(map, BrowserOrientation.LEFT);
		addBrowserActions(map, BrowserOrientation.RIGHT);
	}

	private void addFileActions(Map<String, Consumer<ActionContext>> map) {
		map.put("q.active.open", c -> openSelectionInActiveBrowser());
		map.put("q.active.rename", c -> openRenameDialog());
		map.put("q.active.create.directory", c -> openCreateDirectoryDialog());
		map.put("q.active.create.file", c -> openCreateFileDialog());
		map.put("q.active.copy", c -> openCopyDialog());
		map.put("q.active.move", c -> openMoveDialog());
		map.put("q.active.delete", c -> openDeleteDialog());

	}

	private void addLocationActions(Map<String, Consumer<ActionContext>> map) {
		map.put("q.active.root", c -> beans.getController().navigateFuzzy(BrowserOrientation.ACTIVE, "/"));
		map.put("q.active.location", c -> openLocationDialog());
		map.put("q.active.jump", c -> openJumpDialog());
		map.put("q.active.open.directory", c -> openDirectory());
		map.put("q.active.open.url", c -> openUrl());
		map.put("q.active.previous", c -> mediator.activeOpenPrevious());
		map.put("q.active.revisit", c -> mediator.activeRedoHistory());
	}

	private void openUrl() {
		open(mediator.getActiveBrowser(components).fileList.getSelectedValue().getVirtualFile());
	}

	private void openJumpDialog() {
		dialogs.input("Jump to:", txt -> engine.executeTemplated("~" + UrlTool.escape(txt)));
	}

	private void openDirectory() {
		open(mediator.getActive());
	}

	private void open(VirtualFile file) {
		DesktopActions.open(file);
	}

	private void openCopyDialog() {
		copyOrMoveDialog("copy");
	}

	private void openMoveDialog() {
		copyOrMoveDialog("move");
	}

	private void copyOrMoveDialog(String action) {
		List<VirtualFileWrapper> selection = mediator.getActiveBrowser(components).fileList.getSelectedValuesList();
		if (selection.isEmpty()) {
			dialogs.message("No files selected!");
			return;
		}
		if (selection.size() == 1) {
			VirtualFile file = selection.get(0).getVirtualFile();
			dialogs.input(action + " to:", file.getName(), newName -> {
				String escaped = UrlTool.escape(newName);
				String destinationDir = mediator.getInactive().getUrl().getUrlEscaped();
				String url = file.getUrl().getUrlEscaped();
				engine.executeRaw(Str.sub("$ -s % % %", action, url, destinationDir, escaped));
			});

		} else {
			dialogs.confirm(confirmFiles(action + " the following files?", selection), () -> {
				engine.executeRaw(Str.sub("$ -u % $", action, createUrlString(selection), "$dir~"));
			});
		}
	}

	private void openDeleteDialog() {
		List<VirtualFileWrapper> selection = mediator.getActiveBrowser(components).fileList.getSelectedValuesList();

		if (selection.isEmpty()) {
			dialogs.message("No files selected!");

		} else {
			dialogs.confirm(confirmFiles("<b>delete the following files?</b>", selection), () -> {
				engine.executeRaw(Str.sub("delete -u %", createUrlString(selection)));
			});
		}
	}

	private String confirmFiles(String question, List<VirtualFileWrapper> selection) {
		StringBuilder message = new StringBuilder("<html>").append(question);
		for (int i = 0; i < 10 & i < selection.size(); i++) {
			message.append("<br/>").append(selection.get(i).getVirtualFile().getName());
		}
		if (selection.size() > 10) {
			message.append("<br/>...");
		}
		return message.toString();
	}

	private void openCreateFileDialog() {
		dialogs.input("name of new file:", name -> engine.executeRaw(Str.sub("new -f $ %", "$dir", escape(name))));
	}

	private void openCreateDirectoryDialog() {
		dialogs.input("name of new directory:", name -> engine.executeRaw(Str.sub("new -d $ %", "$dir", escape(name))));
	}

	private String escape(String name) {
		return UrlTool.escape(name);
	}

	private void openRenameDialog() {
		VirtualFileWrapper file = mediator.getActiveBrowser(components).fileList.getSelectedValue();

		if (file == null) {
			dialogs.message("No file selected!");

		} else {
			String original = file.getVirtualFile().getName();
			dialogs.input("rename to:", original, name -> {
				String url = file.getVirtualFile().getUrl().getUrlEscaped();
				String parentUrl = file.getVirtualFile().getParentFile().get().getUrl().getUrlEscaped();
				engine.executeRaw(Str.sub("move -s % % %", url, parentUrl, UrlTool.escape(name)));
			});
		}
	}

	private void openLocationDialog() {
		dialogs.input("open location:", location -> {
			beans.getController().navigateFuzzy(BrowserOrientation.ACTIVE, location);
		});
	}

	private void addBrowserActions(Map<String, Consumer<ActionContext>> map, BrowserOrientation orientation) {
		String lower = orientation.name().toLowerCase();
		map.put("q." + lower + ".parent", c -> {
			Optional<? extends VirtualFile> optional = mediator.getSelectedDirectory(orientation).getParentFile();
			if (optional.isPresent()) {
				mediator.setLocation(orientation, optional.get());
			}
		});
		map.put("q.browser.gained." + lower, c -> browserFocus(orientation));
		map.put("q.browser.click." + lower, c -> updateStatusLabelWithFileSize());
		map.put("q.browser.key.release." + lower, c -> updateStatusLabelWithFileSize());
	}

	private void browserFocus(BrowserOrientation orientation) {
		mediator.setActive(orientation);
		if (mediator.isLeftActive()) {
			beans.getViewActions().focusLeft();
		} else {
			beans.getViewActions().focusRight();
		}
	}

	private void updateStatusLabelWithFileSize() {
		VirtualFileWrapper file = mediator.getActiveBrowser(components).fileList.getSelectedValue();
		if (file != null) {
			String size = FileSize.humanReadableFileSize(file.getVirtualFile().length());
			String name = file.getVirtualFile().getName();
			String label = file.getVirtualFile().isFile() ? name + " (" + size + ")" : name;
			LabelStyler.styleSubtle(components.assistLabel);
			components.assistLabel.setText(label);
		}
	}

	private void openSelectionInActiveBrowser() {
		BrowserComponents bw = mediator.getActiveBrowser(components);
		log.debug("Opening selection for $", bw.orientation);
		new JListSelection<>(bw.fileList).getFirstSelected().ifPresent(wrapper -> {
			openInActiveBrowser(wrapper.getVirtualFile());
		});
	}

	private void openInActiveBrowser(VirtualFile file) {
		if (file.isDirectory()) {
			mediator.setLocation(BrowserOrientation.ACTIVE, file);

		} else if (file instanceof SystemFile) {
			DesktopActions.open(file);
		}
	}

	static String createUrlString(List<VirtualFileWrapper> selection) {
		StringBuilder builder = new StringBuilder(selection.get(0).getVirtualFile().getUrl().getUrlEscaped());
		for (int i = 1; i < selection.size(); i++) {
			builder.append(" ").append(selection.get(i).getVirtualFile().getUrl().getUrlEscaped());
		}
		return builder.toString();
	}
}