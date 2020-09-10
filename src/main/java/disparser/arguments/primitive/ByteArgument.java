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
	public ParsedArgument<Byte> parse(ArgumentReader reader) throws Exception {
		return ParsedArgument.parse(reader.nextByte());
	}

}
