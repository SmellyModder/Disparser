package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class StringArgument implements Argument<String> {

	private StringArgument() {}
	
	public static StringArgument get() {
		return new StringArgument();
	}
	
	@Override
	public ParsedArgument<String> parse(ArgumentReader reader) {
		return ParsedArgument.parse(reader.nextArgument());
	}

}