package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * A simple argument for parsing a number.
 * This allows for more broad use of number values and not just targeting ints, floats, doubles, etc.
 *
 * @author Luke Tonon
 */
public final class NumberArgument implements Argument<Number> {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private NumberArgument() {}

    /**
     * @return The default instance.
     */
    public static NumberArgument get() {
        return new NumberArgument();
    }

    @Override
    public ParsedArgument<Number> parse(ArgumentReader reader) {
        String nextArgument = reader.nextArgument();
        try {
            Number number = NUMBER_FORMAT.parse(nextArgument);
            return ParsedArgument.parse(number);
        } catch (ParseException e) {
            return ParsedArgument.parseError("%s is not a valid number", nextArgument);
        }
    }
}
