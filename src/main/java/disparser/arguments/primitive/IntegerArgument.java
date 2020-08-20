package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing integers.
 * 
 * @author Luke Tonon
 */
public final class IntegerArgument implements Argument<Integer> {

	private IntegerArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static IntegerArgument get() {
		return new IntegerArgument();
	}
	
	@Override
	public ParsedArgument<Integer> parse(ArgumentReader reader) {
		Integer integer = reader.nextInt();
		return integer != null ? ParsedArgument.parse(integer) : ParsedArgument.parseError("`%s` is not a valid integer", reader.getCurrentMessageComponent());
	}

}