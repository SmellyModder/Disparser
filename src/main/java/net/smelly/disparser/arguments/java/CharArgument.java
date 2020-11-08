package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing characters.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class CharArgument implements Argument<Character> {

	private CharArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static CharArgument get() {
		return new CharArgument();
	}

	@Override
	public ParsedArgument<Character> parse(MessageReader reader) throws CommandSyntaxException {
		return ParsedArgument.parse(reader.nextChar());
	}

}