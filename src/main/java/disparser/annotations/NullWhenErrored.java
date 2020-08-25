package disparser.annotations;

import disparser.ParsedArgument;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated over fields and methods to signify that the element it's annotated over is null when an error occurs when parsing a {@link ParsedArgument}.
 * Moreover, when {@link ParsedArgument#getErrorMessage()} is not null the {@link ParsedArgument#getResult()} will be null, and this is a signifier for that.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface NullWhenErrored {}