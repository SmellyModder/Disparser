package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple argument for parsing bytes.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class ByteArgument implements Argument<Byte> {
	private static final ByteArgument DEFAULT = new ByteArgument(Byte.MIN_VALUE, Byte.MAX_VALUE);
	private final byte minimum;
	private final byte maximum;

	private ByteArgument(byte minimum, byte maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	/**
	 * @return The default instance.
	 */
	public static ByteArgument get() {
		return DEFAULT;
	}

	/**
	 * Creates a new {@link ByteArgument} that clamps the parsable value.
	 *
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return a new {@link ByteArgument} that clamps the parsable value.
	 */
	public static ByteArgument getClamped(byte min, byte max) {
		return new ByteArgument(min, max);
	}

	/**
	 * Creates a new {@link ByteArgument} that minimums the parsable value.
	 *
	 * @param min Minimum value
	 * @return a new {@link ByteArgument} that minimums the parsable value.
	 */
	public static ByteArgument getMin(byte min) {
		return new ByteArgument(min, Byte.MAX_VALUE);
	}

	/**
	 * Creates a new {@link ByteArgument} that maxes the parsable value.
	 *
	 * @param max Max value
	 * @return a new {@link ByteArgument} that maxes the parsable value.
	 */
	public static ByteArgument getMax(byte max) {
		return new ByteArgument(Byte.MIN_VALUE, max);
	}

	@Nonnull
	@Override
	public ParsedArgument<Byte> parse(MessageReader reader) throws CommandException {
		byte abyte = reader.nextByte();
		if (abyte > this.maximum) {
			throw reader.getExceptionProvider().getValueTooHighException().create(abyte, this.maximum);
		} else if (abyte < this.minimum) {
			throw reader.getExceptionProvider().getValueTooLowException().create(abyte, this.minimum);
		}
		return ParsedArgument.parse(abyte);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ByteArgument that = (ByteArgument) o;
		return this.minimum == that.minimum && this.maximum == that.maximum;
	}

	@Override
	public int hashCode() {
		return 31 * this.minimum + this.maximum;
	}

	@Override
	public String toString() {
		return "ByteArgument{" +
				"minimum=" + (this.minimum == Byte.MIN_VALUE ? "undefined" : this.minimum) +
				", maximum=" + (this.maximum == Byte.MAX_VALUE ? "undefined" : this.maximum) +
				'}';
	}
}
