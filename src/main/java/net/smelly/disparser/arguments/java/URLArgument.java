package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An argument for parsing URLs.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class URLArgument implements Argument<URL> {
	private static final URLArgument DEFAULT = new URLArgument();

	private URLArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static URLArgument get() {
		return DEFAULT;
	}

	@Nonnull
	@Override
	public ParsedArgument<URL> parse(MessageReader reader) throws CommandException {
		String next = reader.nextArgument();
		if (next.startsWith("<") && next.endsWith(">")) {
			next = next.substring(1, next.length() - 1);
		}
		try {
			return ParsedArgument.parse(new URL(next));
		} catch (MalformedURLException e) {
			throw reader.getExceptionProvider().getInvalidURLException().create(next);
		}
	}

	@Override
	public String toString() {
		return "URLArgument{}";
	}
}
