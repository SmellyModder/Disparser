package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing strings.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class StringArgument implements Argument<String> {
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
		return new StringArgument(0, Integer.MAX_VALUE);
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

	@Override
	public ParsedArgument<String> parse(ArgumentReader reader) throws Exception {
		String nextArgument = reader.nextArgument();
		if (nextArgument.length() > this.maxChars) {
			throw reader.getExceptionProvider().getTooHighStringLengthException().create(nextArgument, this.maxChars);
		} else if (nextArgument.length() < this.minChars) {
			throw reader.getExceptionProvider().getTooLowStringLengthException().create(nextArgument, this.maxChars);
		}
		return ParsedArgument.parse(nextArgument);
	}
}