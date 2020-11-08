package net.smelly.disparser;

import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Implemented on classes to be used as arguments for parsing components of messages.
 *
 * @param <T> Type of this argument
 * @author Luke Tonon
 * @see ConfiguredArgument
 */
@ThreadSafe
public interface Argument<T> {
	/**
	 * Parses the argument into a {@link ParsedArgument}
	 *
	 * @param reader An {@link MessageReader} to use in parsing for this argument.
	 * @return A {@link ParsedArgument} containing the parsed object or {@link ParsedArgument#empty()}.
	 * @throws CommandSyntaxException If an exception occurs trying to parse the argument.
	 */
	ParsedArgument<T> parse(MessageReader reader) throws CommandSyntaxException;

	/**
	 * Creates a new {@link ConfiguredArgument} instance with a given name, description, and optional marker.
	 *
	 * @param name        The name for the {@link ConfiguredArgument}.
	 * @param description The description for the {@link ConfiguredArgument}.
	 * @param optional    If this {@link ConfiguredArgument} is optional.
	 * @return A new {@link ConfiguredArgument} instance with a given name, description, and optional marker.
	 */
	default ConfiguredArgument<T> withConfiguration(CommandMessage name, CommandMessage description, boolean optional) {
		return new ConfiguredArgument<>(this, name, description, optional);
	}
}