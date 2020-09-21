package disparser.arguments.java;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import disparser.feedback.DisparserExceptions;

/**
 * A simple argument for parsing bytes.
 * 
 * @author Luke Tonon
 */
public final class ByteArgument implements Argument<Byte> {
	private final byte minimum;
	private final byte maximum;

	private ByteArgument(byte minimum, byte maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	/**
	 * @return The default instance.
	 */
	public static ByteArgument get() {
		return new ByteArgument(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

	public static ByteArgument getClamped(byte min, byte max) {
		return new ByteArgument(min, max);
	}

	public static ByteArgument getMin(byte min) {
		return new ByteArgument(min, Byte.MAX_VALUE);
	}

	public static ByteArgument getMax(byte max) {
		return new ByteArgument(Byte.MIN_VALUE, max);
	}
	
	@Override
	public ParsedArgument<Byte> parse(ArgumentReader reader) throws Exception {
		byte abyte = reader.nextByte();
		if (abyte > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(abyte, this.maximum);
		} else if (abyte < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(abyte, this.minimum);
		}
		return ParsedArgument.parse(abyte);
	}
}
