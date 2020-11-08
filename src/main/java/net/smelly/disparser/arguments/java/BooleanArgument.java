package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing booleans.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class BooleanArgument implements Argument<Boolean> {

	private BooleanArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static BooleanArgument get() {
		return new BooleanArgument();
	}

	@Override
	public ParsedArgument<Boolean> parse(MessageReader reader) {
		return ParsedArgument.parse(reader.nextBoolean());
	}

}