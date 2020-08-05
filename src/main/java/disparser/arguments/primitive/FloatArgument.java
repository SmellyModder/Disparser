package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class FloatArgument implements Argument<Float> {

	private FloatArgument() {}
	
	public static FloatArgument get() {
		return new FloatArgument();
	}
	
	@Override
	public ParsedArgument<Float> parse(ArgumentReader reader) {
		Float nextFloat = reader.nextFloat();
		return nextFloat != null ? ParsedArgument.parse(nextFloat) : ParsedArgument.parseError("`" + reader.getCurrentMessageComponent() + "` is not a valid float");
	}

}