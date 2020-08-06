package disparser.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import disparser.CommandHandler;

/**
 * Used to merge or overwrite a command instance's aliases when registering it to a {@link CommandHandler}.
 * 
 * @author Luke Tonon
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Aliases {
	/**
	 * This will merge or overwrite the command's existing aliases depending on the value of {@link #mergeAliases()}.
	 * @return The aliases for the command.
	 */
	public String[] value();
	
	/**
	 * @return If it should merge these aliases with the existing aliases.
	 */
	public boolean mergeAliases() default false;
}