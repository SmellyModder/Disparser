package net.smelly.disparser;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A container object for holding a parsed result from an {@link Argument}.
 *
 * @param <A> The type for this parsed argument.
 * @author Luke Tonon
 */
public final class ParsedArgument<A> {
	private final A result;

	private ParsedArgument(@Nonnull A readArgument) {
		this.result = readArgument;
	}

	/**
	 * @param result The result.
	 * @param <A>    The type of the result.
	 * @return A new {@link ParsedArgument} that contains a non-null result.
	 * @throws NullPointerException if value is null
	 */
	public static <A> ParsedArgument<A> parse(@Nonnull A result) {
		Objects.requireNonNull(result);
		return new ParsedArgument<>(result);
	}

	/**
	 * @return The parsed result.
	 */
	@Nonnull
	public A getResult() {
		return this.result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ParsedArgument<?> that = (ParsedArgument<?>) o;
		return Objects.equals(this.result, that.result);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.result);
	}

	@Override
	public String toString() {
		return String.format("ParsedArgument[%s]", this.result);
	}
}