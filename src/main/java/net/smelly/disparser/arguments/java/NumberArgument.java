package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * A simple argument for parsing a number.
 * This allows for more broad use of number values and not just targeting ints, floats, doubles, etc.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class NumberArgument implements Argument<Number> {
	private static final ThreadLocal<NumberFormat> NUMBER_FORMAT = ThreadLocal.withInitial(NumberFormat::getInstance);
	private static final NumberArgument DEFAULT = new NumberArgument(Double.MIN_VALUE, Double.MAX_VALUE);
	private final double minimum;
	private final double maximum;

	private NumberArgument(double minimum, double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static NumberArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link NumberArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link NumberArgument} that clamps the parsable value.
	 */
	public static NumberArgument getClamped(double min, double max) {
		return new NumberArgument(min, max);
	}

	/**
	 * Creates a new {@link NumberArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link NumberArgument} that minimums the parsable value.
	 */
	public static NumberArgument getMin(double min) {
		return new NumberArgument(min, Double.MAX_VALUE);
	}

	/**
	 * Creates a new {@link NumberArgument} that maxes the parsable value.
	 *
	 * @param max Maximum value
	 * @return a new {@link NumberArgument} that maxes the parsable value.
	 */
	public static NumberArgument getMax(double max) {
		return new NumberArgument(Double.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Number> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			try {
				Number number = NUMBER_FORMAT.get().parse(arg);
				double adouble = number.doubleValue();
				if (adouble > this.maximum) {
					throw reader.getExceptionProvider().getValueTooHighException().create(adouble, this.maximum);
				} else if (adouble < this.minimum) {
					throw reader.getExceptionProvider().getValueTooHighException().create(adouble, this.minimum);
				}
				return ParsedArgument.parse(number);
			} catch (ParseException e) {
				throw reader.getExceptionProvider().getInvalidNumberException().create(arg);
			}
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		NumberArgument that = (NumberArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return (int) (31 * this.minimum + this.maximum);
	}

	@Override
	public String toString() {
		return "NumberArgument{" +
				"minimum=" + (this.minimum == Double.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Double.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}
