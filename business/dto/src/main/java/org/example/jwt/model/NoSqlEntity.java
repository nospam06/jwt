package org.example.jwt.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface NoSqlEntity {
	String type();

	String id() default "id";

	String value() default "value";

	boolean idNumeric() default false;

	String index() default "";
}
