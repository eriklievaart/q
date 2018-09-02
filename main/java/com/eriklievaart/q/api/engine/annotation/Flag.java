package com.eriklievaart.q.api.engine.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to mark the command line flags of a ShellCommand. Every ShellCommand must add this annotation
 * to all methods that represent flags.
 * 
 * @author Erik Lievaart
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Flag {

	/** The default values to assign to the arguments of a flag. */
	String[] values() default {};

	/** Assigns the flag to a flag group. Flags with the same group name are mutually exclusive. */
	String group() default "";

	/**
	 * Primary flags are on by default. Primary flags are set to inactive if another flag in the same group is entered
	 * on the command line. Only one flag in a group can be primary (since flags in a group are mutually exclusive).
	 */
	boolean primary() default false;
}
