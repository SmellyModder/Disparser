package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing characters.
 * 
 * @author Luke Tonon
 */
public final class CharArgument implements Argument<Character> {

	private CharArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static CharArgument get() {
		return new CharArgument();
	}
	
	@Override
	public ParsedArgument<Character> parse(ArgumentReader reader) {
		Character character = reader.nextChar();
		return character != null ? ParsedArgument.parse(character) : ParsedArgument.parseError("`%s` exceeds one character", reader.getCurrentMessageComponent());
	}

}