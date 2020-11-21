package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing shorts.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class ShortArgument implements Argument<Short> {
	private static final ShortArgument DEFAULT = new ShortArgument(Short.MIN_VALUE, Short.MAX_VALUE);
	private final short minimum;
	private final short maximum;

	private ShortArgument(short minimum, short maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static ShortArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link ShortArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link ShortArgument} that clamps the parsable value.
	 */
	public static ShortArgument getClamped(byte min, byte max) {
		return new ShortArgument(min, max);
	}

	/**
	 * Creates a new {@link ShortArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link ShortArgument} that minimums the parsable value.
	 */
	public static ShortArgument getMin(byte min) {
		return new ShortArgument(min, Short.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ShortArgument} that maxes the parsable value.
	 *
	 * @param max Maximum value
	 * @return a new {@link ShortArgument} that maxes the parsable value.
	 */
	public static ShortArgument getMax(byte max) {
		return new ShortArgument(Short.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Short> parse(MessageReader reader) throws CommandException {
		short ashort = reader.nextShort();
		if (ashort > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(ashort, this.maximum);
		} else if (ashort < this.minimum) {
			throw reader.getExceptionProvider().getValueTooLowException().create(ashort, this.minimum);
		}
		return ParsedArgument.parse(ashort);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ShortArgument that = (ShortArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return 31 * (int) this.minimum + (int) this.maximum;
	}

	@Override
	public String toString() {
		return "ShortArgument{" +
				"minimum=" + (this.minimum == Short.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Short.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}