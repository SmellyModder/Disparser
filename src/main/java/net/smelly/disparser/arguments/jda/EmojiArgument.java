package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.entities.Activity;
import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument for parsing an {@link Activity.Emoji}.
 * {@link Activity.Emoji} is an object for representing an emoji.
 * @see Activity.Emoji
 *
 * @author Luke Tonon
 */
public final class EmojiArgument implements Argument<Activity.Emoji> {
	private static final Pattern UNICODE_EMOJI_PATTERN = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
	private static final Pattern CUSTOM_EMOJI_PATTERN = Pattern.compile("<(a?):(\\w+):(\\d+)>");

	/**
	 * @return The default instance.
	 */
	public static EmojiArgument get() {
		return new EmojiArgument();
	}

	@Override
	public ParsedArgument<Activity.Emoji> parse(ArgumentReader reader) throws Exception {
		String string = reader.nextArgument();
		List<Activity.Emoji> emojiCount = getEmojis(string);
		if (emojiCount.size() == 1) {
			return ParsedArgument.parse(new Activity.Emoji(string));
		} else {
			Matcher matcher = CUSTOM_EMOJI_PATTERN.matcher(string);
			if (matcher.find()) {
				try {
					return ParsedArgument.parse(new Activity.Emoji(matcher.group(2), Long.parseLong(matcher.group(3)), !matcher.group(1).isEmpty()));
				} catch (NumberFormatException e) {
					throw DisparserExceptions.INVALID_EMOJI_EXCEPTION.create(string);
				}
			}
		}
		throw DisparserExceptions.INVALID_EMOJI_EXCEPTION.create(string);
	}

	/**
	 * Will be used for multiple emoji parsing support Later.
	 */
	private static List<Activity.Emoji> getEmojis(String string) throws UnsupportedEncodingException {
		byte[] utf8Bytes = string.getBytes("UTF-8");
		String convertedString = new String(utf8Bytes, "UTF-8");
		Matcher matcher = UNICODE_EMOJI_PATTERN.matcher(convertedString);
		List<Activity.Emoji> matchList = new ArrayList<>();
		while (matcher.find()) {
			matchList.add(new Activity.Emoji(matcher.group()));
		}
		return matchList;
	}
}
