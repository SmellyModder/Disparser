package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class ShortArgument implements Argument<Short> {

	private ShortArgument() {}
	
	public static ShortArgument get() {
		return new ShortArgument();
	}
	
	@Override
	public ParsedArgument<Short> parse(ArgumentReader reader) {
		Short nextShort = reader.nextShort();
		return nextShort != null ? ParsedArgument.parse(nextShort) : ParsedArgument.parseError("`" + reader.getCurrentMessageComponent() + "` is not a valid short");
	}

}