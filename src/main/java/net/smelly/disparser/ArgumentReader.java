package net.smelly.disparser;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;
import net.smelly.disparser.util.MessageUtil;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Used to read a {@link Message} and parse {@link Argument}s from it.
 * <p> Stores the channel of the message and stores the message down into its individual components </p>
 *
 * @author Luke Tonon
 */
public final class ArgumentReader {
	private final BuiltInExceptionProvider exceptionProvider;
	private final Message message;
	private final String[] messageComponents;
	private int currentComponent;

	public ArgumentReader(BuiltInExceptionProvider exceptionProvider, Message message, String[] messageComponents) {
		this.exceptionProvider = exceptionProvider;
		this.message = message;
		this.messageComponents = messageComponents;
	}

	/**
	 * Creates an ArgumentReader for a {@link Message} with a {@link BuiltInExceptionProvider}.
	 *
	 * @param exceptionProvider The {@link BuiltInExceptionProvider} for this {@link ArgumentReader}.
	 * @param message           The {@link Message} for this {@link ArgumentReader}.
	 * @return {@link ArgumentReader} for the message.
	 */
	public static ArgumentReader create(final BuiltInExceptionProvider exceptionProvider, final Message message) {
		return new ArgumentReader(exceptionProvider, message, message.getContentRaw().split(" "));
	}

	/**
	 * @return This reader's {@link BuiltInExceptionProvider}.
	 */
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * @return The {@link Message} belonging to this {@link ArgumentReader}.
	 */
	public Message getMessage() {
		return this.message;
	}

	/**
	 * @return - The split up message components for this {@link ArgumentReader}.
	 */
	public String[] getMessageComponents() {
		return this.messageComponents;
	}

	/**
	 * @return - The current component index.
	 */
	public int getCurrentComponent() {
		return this.currentComponent;
	}

	/**
	 * @return - The current message component.
	 */
	public String getCurrentMessageComponent() {
		return this.messageComponents[this.currentComponent];
	}

	public Integer nextInt() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Integer.parseInt(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidIntegerException().create(nextArg);
		}
	}

	public Long nextLong() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Long.parseLong(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidLongException().create(nextArg);
		}
	}

	public Character nextChar() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		if (nextArg.length() > 1) {
			throw this.exceptionProvider.getInvalidCharException().create(nextArg);
		}
		return nextArg.charAt(0);
	}

	public Short nextShort() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Short.parseShort(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidShortException().create(nextArg);
		}
	}

	public Byte nextByte() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Byte.parseByte(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidByteException().create(nextArg);
		}
	}

	public Float nextFloat() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Float.parseFloat(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidFloatException().create(nextArg);
		}
	}

	public Double nextDouble() throws CommandSyntaxException {
		String nextArg = this.nextArgument();
		try {
			return Double.parseDouble(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidDoubleException().create(nextArg);
		}
	}

	public boolean nextBoolean() {
		return Boolean.parseBoolean(this.nextArgument());
	}

	/**
	 * Used to convert strings to non-primitive type arguments.
	 *
	 * @param parser - A {@link Function} to parse the next string argument to an {@link ParsedArgument}.
	 * @param <A>    - The type of the argument.
	 * @return The object({@link A}) read from the reader.
	 */
	public <A> ParsedArgument<A> parseNextArgument(final Parser<A> parser) throws Exception {
		return parser.parse(this.nextArgument());
	}

	/**
	 * Tries to parse the next argument in the message.
	 * If it fails to parse the next argument it will not shift the {@link #currentComponent} forward.
	 *
	 * @param argument - The argument to try to parse.
	 * @return The parsed argument. If it fails, the parsed argument's result will be null and an error message will be included in the parsed argument.
	 */
	public <A> ParsedArgument<A> tryToParseArgument(Argument<A> argument) {
		int prevComponent = this.currentComponent;
		try {
			return argument.parse(this);
		} catch (Exception exception) {
			this.currentComponent -= (this.currentComponent - prevComponent);
			return ParsedArgument.empty();
		}
	}

	/**
	 * Gets the next argument in the message's components.
	 * <p> Should ideally only be called once in {@link Argument#parse(ArgumentReader)}. </p>
	 *
	 * @return The next argument.
	 */
	public String nextArgument() {
		this.currentComponent++;
		if (this.currentComponent > this.messageComponents.length - 1) {
			throw new IndexOutOfBoundsException(this.currentComponent + MessageUtil.getOrdinalForInteger(this.currentComponent) + " component doesn't exist!");
		}
		return this.messageComponents[this.currentComponent];
	}

	/**
	 * If the reader has a next argument in its components.
	 *
	 * @return If the reader has a next argument in its components.
	 */
	public boolean hasNextArg() {
		return this.currentComponent + 1 <= this.messageComponents.length - 1;
	}

	@Nullable
	public Guild getGuild() {
		return this.message.isFromGuild() ? this.message.getGuild() : null;
	}

	@FunctionalInterface
	public interface Parser<A> {
		ParsedArgument<A> parse(String arg) throws Exception;
	}
}