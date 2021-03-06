package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing doubles.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class DoubleArgument implements Argument<Double> {
	private static final DoubleArgument DEFAULT = new DoubleArgument(Double.MIN_VALUE, Double.MAX_VALUE);
	private final double minimum;
	private final double maximum;

	private DoubleArgument(double minimum, double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static DoubleArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link DoubleArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link DoubleArgument} that clamps the parsable value.
	 */
	public static DoubleArgument getClamped(double min, double max) {
		return new DoubleArgument(min, max);
	}

	/**
	 * Creates a new {@link DoubleArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link DoubleArgument} that minimums the parsable value.
	 */
	public static DoubleArgument getMin(double min) {
		return new DoubleArgument(min, Double.MAX_VALUE);
	}

	/**
	 * Creates a new {@link DoubleArgument} that maxes the parsable value.
	 *
	 * @param max Max value
	 * @return a new {@link DoubleArgument} that maxes the parsable value.
	 */
	public static DoubleArgument getMax(double max) {
		return new DoubleArgument(Double.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Double> parse(MessageReader reader) throws CommandException {
		double adouble = reader.nextDouble();
		if (adouble > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(adouble, this.maximum);
		} else if (adouble < this.minimum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(adouble, this.minimum);
		}
		return ParsedArgument.parse(adouble);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		DoubleArgument that = (DoubleArgument) o;
		return this.maximum == that.maximum && this.minimum == that.minimum;
	}

	@Override
	public int hashCode() {
		return (int) (31 * this.minimum + this.maximum);
	}

	@Override
	public String toString() {
		return "DoubleArgument{" +
				"minimum=" + (this.minimum == Double.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Double.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}