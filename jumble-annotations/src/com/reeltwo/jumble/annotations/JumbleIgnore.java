package com.reeltwo.jumble.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this annotation to a method to indicate that no mutation
 * points should be used from inside that block. This allows more
 * fine-grained exclusion than specifying a global-method ignore on
 * the jumble command line. You can also apply the annotation to an
 * entire class if you wish.
 * 
 * @author Len Trigg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface JumbleIgnore {
}
