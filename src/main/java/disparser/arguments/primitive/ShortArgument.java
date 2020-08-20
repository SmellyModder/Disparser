package disparser.arguments.primitive;

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
	public ParsedArgument<Short> parse(ArgumentReader reader) {
		Short nextShort = reader.nextShort();
		return nextShort != null ? ParsedArgument.parse(nextShort) : ParsedArgument.parseError("`%s` is not a valid short", reader.getCurrentMessageComponent());
	}

}