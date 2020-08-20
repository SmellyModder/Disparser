package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing booleans.
 * 
 * @author Luke Tonon
 */
public final class BooleanArgument implements Argument<Boolean> {
	
	private BooleanArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static BooleanArgument get() {
		return new BooleanArgument();
	}
	
	@Override
	public ParsedArgument<Boolean> parse(ArgumentReader reader) {
		return ParsedArgument.parse(reader.nextBoolean());
	}
	
}