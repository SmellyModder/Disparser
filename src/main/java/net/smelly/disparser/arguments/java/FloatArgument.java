package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing floats.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class FloatArgument implements Argument<Float> {
	private static final FloatArgument DEFAULT = new FloatArgument(Float.MIN_VALUE, Float.MAX_VALUE);
	private final float minimum;
	private final float maximum;

	private FloatArgument(float minimum, float maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static FloatArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link FloatArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link FloatArgument} that clamps the parsable value.
	 */
	public static FloatArgument getClamped(float min, float max) {
		return new FloatArgument(min, max);
	}

	/**
	 * Creates a new {@link FloatArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link FloatArgument} that minimums the parsable value.
	 */
	public static FloatArgument getMin(float min) {
		return new FloatArgument(min, Float.MAX_VALUE);
	}

	/**
	 * Creates a new {@link FloatArgument} that maxes the parsable value.
	 *
	 * @param max Max value
	 * @return a new {@link FloatArgument} that maxes the parsable value.
	 */
	public static FloatArgument getMax(float max) {
		return new FloatArgument(Float.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Float> parse(MessageReader reader) throws CommandException {
		float afloat = reader.nextFloat();
		if (afloat > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(afloat, this.maximum);
		} else if (afloat < this.minimum) {
			throw reader.getExceptionProvider().getValueTooLowException().create(afloat, this.minimum);
		}
		return ParsedArgument.parse(afloat);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		FloatArgument that = (FloatArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return (int) (31 * this.minimum + this.maximum);
	}

	@Override
	public String toString() {
		return "FloatArgument{" +
				"minimum=" + (this.minimum == Float.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Float.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}