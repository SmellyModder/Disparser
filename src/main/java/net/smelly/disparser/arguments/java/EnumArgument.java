package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;

import javax.annotation.concurrent.ThreadSafe;

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

	@Override
	public ParsedArgument<E> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			for (E type : this.values) {
				if (type.toString().equalsIgnoreCase(arg)) {
					return ParsedArgument.parse(type);
				}
			}
			throw reader.getExceptionProvider().getInvalidEnumException().create(arg);
		});
	}
}