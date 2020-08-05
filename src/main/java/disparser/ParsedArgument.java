package disparser;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class holding a parsed result from an {@link Argument} and possibly an error message.
 * <p> This class works as an 'either' system, i.e. a parsed result and error message cannot both be present. 
 * This effectively means that if there is no error message then the result is present and vise-versa. </p>
 * 
 * @author Luke Tonon
 *
 * @param <A> - The type for this parsed argument.
 */
public final class ParsedArgument<A> {
	@Nullable
	private final A result;
	@Nullable
	private final String errorMessage;
	
	private ParsedArgument(@Nullable final A readArgument, @Nullable final String errorMessage) {
		this.result = readArgument;
		this.errorMessage = errorMessage;
	}
	
	@Nullable
	public A getResult() {
		return this.result;
	}
	
	@Nullable
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public A getOrOtherResult(@Nonnull A other) {
		return this.result == null ? other : (A) this.result;
	}
	
	public void ifHasResult(Consumer<A> consumer) {
		if (this.result != null) consumer.accept(this.result);
	}
	
	public static <A> ParsedArgument<A> parse(@Nonnull final A result) {
		return new ParsedArgument<>(result, null);
	}
	
	public static <A> ParsedArgument<A> parseError(@Nonnull final String errorMessage) {
		return new ParsedArgument<>(null, errorMessage);
	}
}