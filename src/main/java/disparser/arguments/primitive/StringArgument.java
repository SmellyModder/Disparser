package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing strings.
 * 
 * @author Luke Tonon
 */
public final class StringArgument implements Argument<String> {

	private StringArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static StringArgument get() {
		return new StringArgument();
	}
	
	@Override
	public ParsedArgument<String> parse(ArgumentReader reader) {
		return ParsedArgument.parse(reader.nextArgument());
	}

}