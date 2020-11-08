package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing integers.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class IntegerArgument implements Argument<Integer> {
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
		return new IntegerArgument(Integer.MIN_VALUE, Integer.MAX_VALUE);
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

	@Override
	public ParsedArgument<Integer> parse(MessageReader reader) throws CommandSyntaxException {
		int integer = reader.nextInt();
		if (integer > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(integer, this.maximum);
		} else if (integer < this.minimum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(integer, this.minimum);
		}
		return ParsedArgument.parse(integer);
	}
}