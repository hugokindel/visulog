package up.visulog.cli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation interface for an option. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
    /** List of names. */
    String[] names();

    /** Description (each index of the array is a line of text). */
    String[] description() default {};

    /** Describes how to use this option. */
    String usage() default "";
}