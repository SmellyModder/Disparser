package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing longs.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class LongArgument implements Argument<Long> {
	private static final LongArgument DEFAULT = new LongArgument(Long.MIN_VALUE, Long.MAX_VALUE);
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
		return DEFAULT;
	}

	/**
	 * Creates a new {@link LongArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link LongArgument} that clamps the parsable value.
	 */
	public static LongArgument getClamped(long min, long max) {
		return new LongArgument(min, max);
	}

	/**
	 * Creates a new {@link LongArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link LongArgument} that minimums the parsable value.
	 */
	public static LongArgument getMin(long min) {
		return new LongArgument(min, Long.MAX_VALUE);
	}

	/**
	 * Creates a new {@link LongArgument} that maxes the parsable value.
	 *
	 * @param max Maximum value
	 * @return a new {@link LongArgument} that maxes the parsable value.
	 */
	public static LongArgument getMax(long max) {
		return new LongArgument(Long.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Long> parse(MessageReader reader) throws CommandException {
		long along = reader.nextLong();
		if (along > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(along, this.maximum);
		} else if (along < this.minimum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(along, this.minimum);
		}
		return ParsedArgument.parse(along);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		LongArgument that = (LongArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return 31 * Long.hashCode(this.minimum) + Long.hashCode(this.maximum);
	}

	@Override
	public String toString() {
		return "LongArgument{" +
				"minimum=" + (this.minimum == Long.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Long.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}