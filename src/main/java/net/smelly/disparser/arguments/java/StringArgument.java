package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing strings.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class StringArgument implements Argument<String> {
	private static final StringArgument DEFAULT = new StringArgument(0, Integer.MAX_VALUE);
	private final int minChars;
	private final int maxChars;

	private StringArgument(int minChars, int maxChars) {
		this.minChars = minChars;
		this.maxChars = maxChars;
	}

	/**
	 * @return The default instance.
	 */
	public static StringArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates new {@link StringArgument} that has a minimum parsable string length.
	 *
	 * @param minChars Minimum amount of characters.
	 * @return A new {@link StringArgument} that has a minimum parsable string length.
	 */
	public static StringArgument getMin(int minChars) {
		return new StringArgument(Math.max(0, minChars), Integer.MAX_VALUE);
	}

	/**
	 * Creates new {@link StringArgument} that has a maximum parsable string length.
	 *
	 * @param maxChars Maximum amount of characters.
	 * @return A new {@link StringArgument} that has a maximum parsable string length.
	 */
	public static StringArgument getMax(int maxChars) {
		return new StringArgument(0, maxChars);
	}

	/**
	 * Creates a new {@link StringArgument} that clamps the parsable string length.
	 *
	 * @param minChars Minimum amount of characters.
	 * @param maxChars Maximum amount of characters allowed.
	 * @return A new {@link StringArgument} that clamps the parsable string length.
	 */
	public static StringArgument getClamped(int minChars, int maxChars) {
		return new StringArgument(Math.max(0, minChars), maxChars);
	}

	@Nonnull
	@Override
	public ParsedArgument<String> parse(MessageReader reader) throws CommandException {
		String nextArgument = reader.nextArgument();
		if (nextArgument.length() > this.maxChars) {
			throw reader.getExceptionProvider().getTooHighStringLengthException().create(nextArgument, this.maxChars);
		} else if (nextArgument.length() < this.minChars) {
			throw reader.getExceptionProvider().getTooLowStringLengthException().create(nextArgument, this.maxChars);
		}
		return ParsedArgument.parse(nextArgument);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		StringArgument that = (StringArgument) o;
		return this.minChars == that.minChars && this.maxChars == that.maxChars;
	}

	@Override
	public int hashCode() {
		return 31 * this.minChars + this.maxChars;
	}

	@Override
	public String toString() {
		return "StringArgument{" +
				"minChars=" + (this.minChars == 0 ? "undefined" : this.minChars) +
				", maxChars=" + (this.maxChars == Integer.MAX_VALUE ? "undefined" : this.maxChars) +
				'}';
	}
}