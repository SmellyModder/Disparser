package net.smelly.disparser.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to merge or overwrite a command instance's aliases when registering it to a {@link net.smelly.disparser.context.handlers.AbstractCommandHandler}.
 * <p>This can also be used as a value holder for an array of aliases.</p>
 *
 * @author Luke Tonon
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Aliases {
	/**
	 * This will merge or overwrite the command's existing aliases depending on the value of {@link #mergeAliases()}.
	 *
	 * @return The aliases for the command.
	 */
	String[] value();

	/**
	 * @return If it should merge these aliases with the existing aliases.
	 */
	boolean mergeAliases() default false;
}