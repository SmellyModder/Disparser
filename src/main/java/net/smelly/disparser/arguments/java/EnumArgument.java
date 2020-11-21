package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;

/**
 * An argument that parses values of an enum by their name.
 *
 * @param <E> - The type of enum.
 * @author Luke Tonon
 */
@ThreadSafe
public final class EnumArgument<E extends Enum<?>> implements Argument<E> {
	private final E[] values;

	private EnumArgument(Class<E> type) {
		this.values = type.getEnumConstants();
	}

	/**
	 * @return An instance containing all the possible values of an enum.
	 */
	public static <E extends Enum<?>> EnumArgument<E> get(Class<E> type) {
		return new EnumArgument<>(type);
	}

	@Nonnull
	@Override
	public ParsedArgument<E> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			for (E type : this.values) {
				if (type.toString().equalsIgnoreCase(arg)) {
					return ParsedArgument.parse(type);
				}
			}
			throw reader.getExceptionProvider().getInvalidEnumException().create(arg);
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		EnumArgument<?> that = (EnumArgument<?>) o;
		return Arrays.equals(this.values, that.values);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.values);
	}

	@Override
	public String toString() {
		return "EnumArgument{" +
				"enum=" + this.values[0].getClass() +
				'}';
	}
}