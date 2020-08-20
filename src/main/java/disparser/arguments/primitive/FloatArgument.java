package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing bytes.
 * 
 * @author Luke Tonon
 */
public final class FloatArgument implements Argument<Float> {

	private FloatArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static FloatArgument get() {
		return new FloatArgument();
	}
	
	@Override
	public ParsedArgument<Float> parse(ArgumentReader reader) {
		Float nextFloat = reader.nextFloat();
		return nextFloat != null ? ParsedArgument.parse(nextFloat) : ParsedArgument.parseError("`%s` is not a valid float", reader.getCurrentMessageComponent());
	}

}