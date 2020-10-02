package up.visulog.cli.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation interface for a command. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /** Name of the command. */
    String name();

    /** Version of the command. */
    String version() default "0.0.0";

    /** Description of the command. */
    String[] description() default {};
}