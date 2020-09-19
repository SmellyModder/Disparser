package disparser.arguments.java;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing shorts.
 * 
 * @author Luke Tonon
 */
public final class ShortArgument implements Argument<Short> {

	private ShortArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static ShortArgument get() {
		return new ShortArgument();
	}
	
	@Override
	public ParsedArgument<Short> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextShort());
	}

}