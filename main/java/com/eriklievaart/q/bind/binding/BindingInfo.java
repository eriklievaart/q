package com.eriklievaart.q.bind.binding;

import javax.swing.KeyStroke;

import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class BindingInfo {
	private LogTemplate log = new LogTemplate(getClass());

	public Long bundleId;
	public String actionId;
	public String componentId;

	public ActionType event;
	public KeyStroke keyPressed;
	public KeyStroke keyReleased;

	public void warn(String message) {
		log.warn("$; skipping binding $:$ -> %", message, componentId, event, actionId);
	}

	public String getUniqueKey() {
		if (keyPressed != null) {
			return strokeToString(keyPressed);
		}
		if (keyReleased != null) {
			return strokeToString(keyReleased);
		}
		return Str.sub("$;$;$", componentId, event, actionId);
	}

	private String strokeToString(KeyStroke key) {
		String stroke = Str.sub("$_$_$", key.getKeyChar(), key.getKeyCode(), key.getModifiers());
		return Str.sub("$;$;$;$", componentId, event, actionId, stroke);
	}
}
