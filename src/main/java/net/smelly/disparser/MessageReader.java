package net.smelly.disparser;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Used to read a {@link Message} and parse {@link Argument}s from it.
 * <p> Stores a {@link Message} and its contents split by spaces. </p>
 *
 * @author Luke Tonon
 */
public final class MessageReader {
	private final BuiltInExceptionProvider exceptionProvider;
	private final Message message;
	private final String[] components;
	private final int length;
	private int index;

	public MessageReader(BuiltInExceptionProvider exceptionProvider, Message message, String[] components) {
		this.exceptionProvider = exceptionProvider;
		this.message = message;
		this.components = components;
		this.length = components.length;
	}

	/**
	 * Creates an ArgumentReader for a {@link Message} with a {@link BuiltInExceptionProvider}.
	 *
	 * @param exceptionProvider The {@link BuiltInExceptionProvider} for this {@link MessageReader}.
	 * @param message           The {@link Message} for this {@link MessageReader}.
	 * @return {@link MessageReader} for the message.
	 */
	public static MessageReader create(final BuiltInExceptionProvider exceptionProvider, final Message message) {
		return new MessageReader(exceptionProvider, message, message.getContentRaw().split("\\s+"));
	}

	/**
	 * @return This reader's {@link BuiltInExceptionProvider}.
	 */
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * @return The {@link Message} belonging to this {@link MessageReader}.
	 */
	public Message getMessage() {
		return this.message;
	}

	/**
	 * @return The split up message components for this {@link MessageReader}.
	 */
	public String[] getComponents() {
		return this.components;
	}

	/**
	 * @return The length of the split message components. Subtract 1 for length of the arguments.
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * @return The current component index.
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * @return The current message component.
	 */
	public String getCurrentComponent() {
		return this.components[this.index];
	}

	public Integer nextInt() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Integer.parseInt(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidIntegerException().create(nextArg);
		}
	}

	public Long nextLong() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Long.parseLong(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidLongException().create(nextArg);
		}
	}

	public Character nextChar() throws CommandException {
		String nextArg = this.nextArgument();
		if (nextArg.length() > 1) {
			throw this.exceptionProvider.getInvalidCharException().create(nextArg);
		}
		return nextArg.charAt(0);
	}

	public Short nextShort() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Short.parseShort(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidShortException().create(nextArg);
		}
	}

	public Byte nextByte() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Byte.parseByte(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidByteException().create(nextArg);
		}
	}

	public Float nextFloat() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Float.parseFloat(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidFloatException().create(nextArg);
		}
	}

	public Double nextDouble() throws CommandException {
		String nextArg = this.nextArgument();
		try {
			return Double.parseDouble(nextArg);
		} catch (NumberFormatException exception) {
			throw this.exceptionProvider.getInvalidDoubleException().create(nextArg);
		}
	}

	public boolean nextBoolean() throws CommandException {
		String nextArg = this.nextArgument();
		if (nextArg.equalsIgnoreCase("true")) {
			return true;
		} else if (nextArg.equalsIgnoreCase("false")) {
			return false;
		}
		throw this.exceptionProvider.getInvalidBooleanException().create(nextArg);
	}

	/**
	 * Used to convert strings to non-primitive type arguments.
	 *
	 * @param parser A {@link Function} to parse the next string argument to an {@link ParsedArgument}.
	 * @param <A>    The type of the argument.
	 * @return The object({@link A}) read from the reader.
	 */
	public <A> ParsedArgument<A> parseNextArgument(Parser<A> parser) throws CommandException {
		return parser.parse(this.nextArgument());
	}

	/**
	 * Gets the next argument in the message's components.
	 * <p> Should ideally only be called once in {@link Argument#parse(MessageReader)}. </p>
	 *
	 * @return The next argument.
	 */
	public String nextArgument() throws CommandException {
		if (this.hasNextArg()) {
			this.index++;
			return this.components[this.index];
		} else {
			this.index++;
			throw this.exceptionProvider.getExpectedArgumentException().create(this.index);
		}
	}

	public String lastArgument() throws CommandException {
		if (this.index <= 0) {
			throw this.exceptionProvider.getExpectedArgumentException().create(this.index - 1);
		}
		return this.components[--this.index];
	}

	/**
	 * If the reader has a next argument in its components.
	 *
	 * @return If the reader has a next argument in its components.
	 */
	public boolean hasNextArg() {
		return this.index < this.length - 1;
	}

	@Nullable
	public Guild getGuild() {
		return this.message.isFromGuild() ? this.message.getGuild() : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		MessageReader reader = (MessageReader) o;
		return this.length == reader.length && Arrays.equals(Arrays.copyOfRange(this.components, 1, this.components.length), Arrays.copyOfRange(reader.components, 1, reader.components.length));
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(this.length);
		result = 31 * result + Arrays.hashCode(Arrays.copyOfRange(this.components, 1, this.components.length));
		return result;
	}

	@FunctionalInterface
	public interface Parser<A> {
		ParsedArgument<A> parse(String arg) throws CommandException;
	}
}