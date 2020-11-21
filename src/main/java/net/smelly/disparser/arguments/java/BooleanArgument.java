package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing booleans.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class BooleanArgument implements Argument<Boolean> {
	private static final BooleanArgument DEFAULT = new BooleanArgument();

	private BooleanArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static BooleanArgument get() {
		return DEFAULT;
	}

	@Nonnull
	@Override
	public ParsedArgument<Boolean> parse(MessageReader reader) throws CommandException {
		return ParsedArgument.parse(reader.nextBoolean());
	}

	@Override
	public String toString() {
		return "BooleanArgument{}";
	}
}