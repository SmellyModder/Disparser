package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class BooleanArgument implements Argument<Boolean> {
	
	private BooleanArgument() {}
	
	public static BooleanArgument get() {
		return new BooleanArgument();
	}
	
	@Override
	public ParsedArgument<Boolean> parse(ArgumentReader reader) {
		return ParsedArgument.parse(reader.nextBoolean());
	}
	
}