package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

/**
 * An argument that parses values of an enum by their name.
 * 
 * @author Luke Tonon
 *
 * @param <E> - The type of enum.
 */
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
	public ParsedArgument<E> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			for (E type : this.values) {
				if (type.toString().equalsIgnoreCase(arg)) {
					return ParsedArgument.parse(type);
				}
			}
			return ParsedArgument.parseError("`%s` is not a valid type", arg);
		});
	}
}