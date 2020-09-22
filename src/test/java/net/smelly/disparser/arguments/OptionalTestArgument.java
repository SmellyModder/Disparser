package net.smelly.disparser.arguments;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.annotations.Optional;

/**
 * Test of optional argument annotation.
 * 
 * @author Luke Tonon
 */
@Optional
public class OptionalTestArgument implements Argument<Integer> {

	@Override
	public ParsedArgument<Integer> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextInt());
	}

}