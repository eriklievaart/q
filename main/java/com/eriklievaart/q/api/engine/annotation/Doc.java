package com.eriklievaart.q.api.engine.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Every ShellCommand and all of their flags are required to have a Doc annotation describing their purpose.
 *
 * @author Erik Lievaart
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Doc {

	/** An explanation of the purpose of a ShellCommand or flag. */
	String value();
}
