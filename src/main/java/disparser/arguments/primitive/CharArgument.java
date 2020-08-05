package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

public class CharArgument implements Argument<Character> {

	private CharArgument() {}
	
	public static CharArgument get() {
		return new CharArgument();
	}
	
	@Override
	public ParsedArgument<Character> parse(ArgumentReader reader) {
		Character character = reader.nextChar();
		return character != null ? ParsedArgument.parse(character) : ParsedArgument.parseError("`" + reader.getCurrentMessageComponent() + "`" + " exceeds one character");
	}

}