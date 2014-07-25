package de.larsgrefer.cli.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author lgrefer
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CliOption {
	char name() default DEFAULT_NAME;
	String longName() default DEFAULT_LONG_NAME;
	boolean required() default false;
	
	public static char DEFAULT_NAME = '\0';
	public static String DEFAULT_LONG_NAME = "#default";
}
