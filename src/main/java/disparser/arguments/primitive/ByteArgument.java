package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * A simple argument for parsing bytes.
 * 
 * @author Luke Tonon
 */
public final class ByteArgument implements Argument<Byte> {

	private ByteArgument() {}
	
	/**
	 * @return The default instance.
	 */
	public static ByteArgument get() {
		return new ByteArgument();
	}
	
	@Override
	public ParsedArgument<Byte> parse(ArgumentReader reader) {
		Byte nextByte = reader.nextByte();
		return nextByte != null ? ParsedArgument.parse(nextByte) : ParsedArgument.parseError("`%s` is not a valid byte", reader.getCurrentMessageComponent());
	}

}
