package net.smelly.disparser;

import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implemented on classes to be used as arguments for parsing components of messages.
 *
 * @param <T> Type of this argument
 * @author Luke Tonon
 * @see ConfiguredArgument
 * @see ParsedArgument
 */
@ThreadSafe
public interface Argument<T> {
	/**
	 * Parses the argument into a {@link ParsedArgument}
	 *
	 * @param reader An {@link MessageReader} to use in parsing for this argument.
	 * @return A {@link ParsedArgument} containing the parsed object.
	 * @throws CommandException If an exception occurs trying to parse the argument.
	 */
	@Nonnull
	ParsedArgument<T> parse(MessageReader reader) throws CommandException;
}