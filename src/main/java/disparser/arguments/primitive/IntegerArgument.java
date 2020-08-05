package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class IntegerArgument implements Argument<Integer> {

	private IntegerArgument() {}
	
	public static IntegerArgument get() {
		return new IntegerArgument();
	}
	
	@Override
	public ParsedArgument<Integer> parse(ArgumentReader reader) {
		Integer integer = reader.nextInt();
		return integer != null ? ParsedArgument.parse(integer) : ParsedArgument.parseError("`" + reader.getCurrentMessageComponent() + "` is not a valid integer");
	}

}