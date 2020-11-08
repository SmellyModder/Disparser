package net.smelly.disparser.arguments;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.annotations.Optional;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

/**
 * Test of optional argument annotation.
 *
 * @author Luke Tonon
 */
@Optional
public final class OptionalTestArgument implements Argument<Integer> {

	@Override
	public ParsedArgument<Integer> parse(MessageReader reader) throws CommandSyntaxException {
		return ParsedArgument.parse(reader.nextInt());
	}

}