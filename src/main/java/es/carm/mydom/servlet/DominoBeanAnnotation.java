package es.carm.mydom.servlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DominoBeanAnnotation {
	String name();
}
