package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * @author Luke Tonon
 *
 * @param <E> - The type of enum.
 */
public final class EnumArgument<E extends Enum<?>> implements Argument<E> {
	private final E[] values;
	
	private EnumArgument(Class<E> type) {
		this.values = type.getEnumConstants();
	}
	
	public static <E extends Enum<?>> EnumArgument<E> get(Class<E> type) {
		return new EnumArgument<>(type);
	}
	
	@Override
	public ParsedArgument<E> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			for (E type : this.values) {
				if (type.toString().equalsIgnoreCase(arg)) {
					return ParsedArgument.parse(type);
				}
			}
			return ParsedArgument.parseError(arg + " is not a valid type");
		});
	}
}