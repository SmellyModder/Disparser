package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing doubles.
 * 
 * @author Luke Tonon
 */
public final class DoubleArgument implements Argument<Double> {

	private DoubleArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static DoubleArgument get() {
		return new DoubleArgument();
	}
	
	@Override
	public ParsedArgument<Double> parse(ArgumentReader reader) {
		Double nextDouble = reader.nextDouble();
		return nextDouble != null ? ParsedArgument.parse(nextDouble) : ParsedArgument.parseError("`%s` is not a valid double", reader.getCurrentMessageComponent());
	}

}