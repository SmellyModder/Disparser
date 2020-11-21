package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing integers.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class IntegerArgument implements Argument<Integer> {
	private static final IntegerArgument DEFAULT = new IntegerArgument(Integer.MIN_VALUE, Integer.MAX_VALUE);
	private final int minimum;
	private final int maximum;

	private IntegerArgument(int minimum, int maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static IntegerArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link IntegerArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link IntegerArgument} that clamps the parsable value.
	 */
	public static IntegerArgument getClamped(int min, int max) {
		return new IntegerArgument(min, max);
	}

	/**
	 * Creates a new {@link IntegerArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link IntegerArgument} that minimums the parsable value.
	 */
	public static IntegerArgument getMin(int min) {
		return new IntegerArgument(min, Integer.MAX_VALUE);
	}

	/**
	 * Creates a new {@link IntegerArgument} that maxes the parsable value.
	 *
	 * @param max Maximum value
	 * @return a new {@link IntegerArgument} that maxes the parsable value.
	 */
	public static IntegerArgument getMax(int max) {
		return new IntegerArgument(Integer.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Integer> parse(MessageReader reader) throws CommandException {
		int integer = reader.nextInt();
		if (integer > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(integer, this.maximum);
		} else if (integer < this.minimum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(integer, this.minimum);
		}
		return ParsedArgument.parse(integer);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		IntegerArgument that = (IntegerArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return 31 * this.minimum + this.maximum;
	}

	@Override
	public String toString() {
		return "IntegerArgument{" +
				"minimum=" + (this.minimum == Integer.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Integer.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}