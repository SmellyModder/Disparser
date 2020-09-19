package disparser.arguments.java;

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
	public ParsedArgument<Double> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextDouble());
	}

}