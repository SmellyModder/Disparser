package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing longs.
 * 
 * @author Luke Tonon
 */
public final class LongArgument implements Argument<Long> {

	private LongArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static LongArgument get() {
		return new LongArgument();
	}
	
	@Override
	public ParsedArgument<Long> parse(ArgumentReader reader) {
		Long nextLong = reader.nextLong();
		return nextLong != null ? ParsedArgument.parse(nextLong) : ParsedArgument.parseError("`%s` is not a valid long", reader.getCurrentMessageComponent());
	}

}