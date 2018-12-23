package com.eriklievaart.q.ui.clipboard;

import java.awt.event.InputEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.q.api.ActionContext;
import com.eriklievaart.q.ui.UiBeanFactory;
import com.eriklievaart.q.ui.context.ContextMediator;
import com.eriklievaart.q.ui.event.EngineEvent;
import com.eriklievaart.q.ui.main.Dialogs;
import com.eriklievaart.toolkit.io.api.SystemClipboard;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class ClipboardActions {

	private ContextMediator mediator;
	private EngineEvent engine;
	private Dialogs dialogs;
	private UiBeanFactory beans;

	public ClipboardActions(UiBeanFactory beans) {
		this.mediator = beans.getContextMediator();
		this.engine = beans.getEngineEvent();
		this.dialogs = beans.getDialogs();
		this.beans = beans;
	}

	public void putActions(Map<String, Consumer<ActionContext>> map) {
		map.put("q.clipboard.cut", c -> ClipboardManager.cutUrls(mediator.getActiveUrlsAsStrings()));
		map.put("q.clipboard.copy", this::copyConsumeEvent);
		map.put("q.clipboard.paste", c -> clipboardPaste());
		map.put("q.clipboard.dir", c -> activeDirToClipboard());
	}

	private void activeDirToClipboard() {
		String path = mediator.getActive().getPath();
		SystemClipboard.writeString(path);
		dialogs.message("On clipboard: " + path);
	}

	private void clipboardPaste() {
		ClipboardTask task = ClipboardManager.getClipboardTask();
		beans.withUrlResolver(resolver -> {
			List<String> invalid = task.getInvalidUrls(resolver);
			if (!invalid.isEmpty()) {
				throw new AssertionException("Unable to read URL's from clipboard: $", invalid);
			}
			if (!task.isEmpty()) {
				String urls = String.join(" ", task.getEscapedUrlList());
				String command = Str.sub("$ -u % $", task.getOperation(), urls, "$dir");
				engine.executeRaw(command);
			}
		});
	}

	private void copyConsumeEvent(ActionContext context) {
		ClipboardManager.copyUrls(mediator.getActiveUrlsAsStrings());
		if (context.getEventObject() instanceof InputEvent) {
			InputEvent event = (InputEvent) context.getEventObject();
			event.consume();
		}
	}
}
