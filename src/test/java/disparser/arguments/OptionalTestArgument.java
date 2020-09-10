package disparser.arguments;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import disparser.annotations.Optional;

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