package disparser.arguments.primitive;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import disparser.feedback.DisparserExceptions;

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
    public ParsedArgument<Number> parse(ArgumentReader reader) throws Exception {
        return reader.parseNextArgument((arg) -> {
            try {
                return ParsedArgument.parse(NUMBER_FORMAT.parse(arg));
            } catch (ParseException e) {
                throw DisparserExceptions.INVALID_NUMBER_EXCEPTION.create(arg);
            }
        });
    }
}
