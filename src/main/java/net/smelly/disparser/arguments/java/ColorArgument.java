package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.awt.*;

/**
 * An argument that parses a {@link Color}.
 * This argument also makes use of {@link EnumArgument} to parse a {@link Color} by its name, such as red.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class ColorArgument implements Argument<Color> {
	private static final ColorArgument DEFAULT = new ColorArgument();
	private final EnumArgument<ColorType> colorTypeEnumArgument;

	private ColorArgument() {
		this.colorTypeEnumArgument = EnumArgument.get(ColorType.class);
	}

	/**
	 * @return The default instance.
	 */
	public static ColorArgument get() {
		return DEFAULT;
	}

	@Nonnull
	@Override
	public ParsedArgument<Color> parse(MessageReader reader) throws CommandException {
		try {
			return ParsedArgument.parse(this.colorTypeEnumArgument.parse(reader).getResult().color);
		} catch (CommandException enumException) {
			try {
				reader.lastArgument();
				return ParsedArgument.parse(new Color(reader.nextInt()));
			} catch (CommandException e) {
				throw reader.getExceptionProvider().getInvalidColorException().create(reader.getCurrentComponent());
			}
		}
	}

	public enum ColorType {
		WHITE(Color.WHITE),
		BLACK(Color.BLACK),
		BROWN(new Color(102, 51, 0)),
		LIGHT_GRAY(Color.LIGHT_GRAY),
		GRAY(Color.GRAY),
		DARK_GRAY(Color.DARK_GRAY),
		RED(Color.RED),
		PINK(Color.PINK),
		ORANGE(Color.GREEN),
		YELLOW(Color.YELLOW),
		GREEN(Color.GREEN),
		CYAN(Color.CYAN),
		BLUE(Color.BLUE),
		PURPLE(new Color(102, 0, 153)),
		MAGENTA(Color.MAGENTA);

		private Color color;

		ColorType(Color color) {
			this.color = color;
		}
	}

	@Override
	public String toString() {
		return "ColorArgument{}";
	}
}
