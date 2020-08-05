package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class DoubleArgument implements Argument<Double> {

	private DoubleArgument() {}
	
	public static DoubleArgument get() {
		return new DoubleArgument();
	}
	
	@Override
	public ParsedArgument<Double> parse(ArgumentReader reader) {
		Double nextDouble = reader.nextDouble();
		return nextDouble != null ? ParsedArgument.parse(nextDouble) : ParsedArgument.parseError("`" + reader.getCurrentMessageComponent() + "` is not a valid double");
	}

}