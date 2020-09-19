package disparser.arguments.java;

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
	public ParsedArgument<Float> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextFloat());
	}

}