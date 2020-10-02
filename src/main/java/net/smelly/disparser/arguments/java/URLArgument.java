package net.smelly.disparser.arguments.java;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.DisparserExceptions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An argument for parsing URLs.
 *
 * @author Luke Tonon
 */
public final class URLArgument implements Argument<URL> {

	private URLArgument() {}

	/**
	 * @return The default instance.
	 */
	public static URLArgument get() {
		return new URLArgument();
	}

	@Override
	public ParsedArgument<URL> parse(ArgumentReader reader) throws Exception {
		String next = reader.nextArgument();
		if (next.startsWith("<") && next.endsWith(">")) {
			next = next.substring(1, next.length() - 1);
		}
		try {
			return ParsedArgument.parse(new URL(next));
		} catch (MalformedURLException e) {
			throw DisparserExceptions.INVALID_URL_EXCEPTION.create(next);
		}
	}

}
