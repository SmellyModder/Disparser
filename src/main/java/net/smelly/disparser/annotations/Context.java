package net.smelly.disparser.annotations;

import net.smelly.disparser.context.CommandContext;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated on {@link net.smelly.disparser.Command} types and fields to get what type of {@link CommandContext} it executes for.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Context {
	/**
	 * @return A class with a type of {@link CommandContext} for this {@link Context}.
	 */
	Class<? extends CommandContext<?>> value();
}
