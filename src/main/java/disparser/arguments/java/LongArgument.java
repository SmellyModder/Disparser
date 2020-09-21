package disparser.arguments.java;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import disparser.feedback.DisparserExceptions;

/**
 * A simple argument for parsing longs.
 * 
 * @author Luke Tonon
 */
public final class LongArgument implements Argument<Long> {
	private final long minimum;
	private final long maximum;

	private LongArgument(long minimum, long maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static LongArgument get() {
		return new LongArgument(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public static LongArgument getClamped(long min, long max) {
		return new LongArgument(min, max);
	}

	public static LongArgument getMin(long min) {
		return new LongArgument(min, Long.MAX_VALUE);
	}

	public static LongArgument getMax(long max) {
		return new LongArgument(Long.MIN_VALUE, max);
	}

	@Override
	public ParsedArgument<Long> parse(ArgumentReader reader) throws Exception {
		long along = reader.nextLong();
		if (along > this.maximum) {
			throw DisparserExceptions.VALUE_TOO_HIGH.create(along, this.maximum);
		} else if (along < this.minimum) {
			throw DisparserExceptions.VALUE_TOO_LOW.create(along, this.minimum);
		}
		return ParsedArgument.parse(along);
	}
}