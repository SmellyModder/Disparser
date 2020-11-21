package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing characters.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class CharArgument implements Argument<Character> {
	private static final CharArgument DEFAULT = new CharArgument();

	private CharArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static CharArgument get() {
		return DEFAULT;
	}

	@Nonnull
	@Override
	public ParsedArgument<Character> parse(MessageReader reader) throws CommandException {
		return ParsedArgument.parse(reader.nextChar());
	}

	@Override
	public String toString() {
		return "CharArgument{}";
	}
}