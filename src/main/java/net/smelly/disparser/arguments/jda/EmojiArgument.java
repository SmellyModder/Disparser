package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.entities.Activity;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument for parsing an {@link Activity.Emoji}.
 * Use {@link EmojiArgument.Multiple} for parsing multiple emojis.
 * {@link Activity.Emoji} is an object for representing an emoji.
 *
 * @author Luke Tonon
 * @see Activity.Emoji
 */
@ThreadSafe
public final class EmojiArgument implements Argument<Activity.Emoji> {
	private static final Pattern UNICODE_EMOJI_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", Pattern.UNICODE_CHARACTER_CLASS);
	private static final Pattern CUSTOM_EMOJI_PATTERN = Pattern.compile("<(a?):(\\w+):(\\d+)>");
	private static final EmojiArgument DEFAULT = new EmojiArgument(true);
	private final boolean allowCustomEmotes;

	private EmojiArgument(boolean allowCustomEmotes) {
		this.allowCustomEmotes = allowCustomEmotes;
	}

	/**
	 * @return The default instance.
	 */
	public static EmojiArgument get() {
		return DEFAULT;
	}

	/**
	 * @return An instance that won't parse custom emojis.
	 */
	public static EmojiArgument getNoCustomEmojis() {
		return new EmojiArgument(false);
	}

	/**
	 * @param allowCustomEmotes Should custom emojis be able to be parsed.
	 * @return An instance that parses multiple emojis.
	 */
	public static EmojiArgument.Multiple getMultiple(boolean allowCustomEmotes) {
		return new EmojiArgument.Multiple(0, Integer.MAX_VALUE, allowCustomEmotes);
	}

	/**
	 * @param minEmojis         The minimum amount of emojis that must be parsed.
	 * @param maxEmojis         The max amount of emojis able to be parsed.
	 * @param allowCustomEmotes Should custom emojis be able to be parsed.
	 * @return An instance that parses multiple emojis.
	 */
	public static EmojiArgument.Multiple getMultipleClamped(int minEmojis, int maxEmojis, boolean allowCustomEmotes) {
		return new EmojiArgument.Multiple(minEmojis, maxEmojis, allowCustomEmotes);
	}

	/**
	 * @param minEmojis         The minimum amount of emojis that must be parsed.
	 * @param allowCustomEmotes Should custom emojis be able to be parsed.
	 * @return An instance that parses multiple emojis.
	 */
	public static EmojiArgument.Multiple getMultipleMin(int minEmojis, boolean allowCustomEmotes) {
		return new EmojiArgument.Multiple(minEmojis, Integer.MAX_VALUE, allowCustomEmotes);
	}

	/**
	 * @param maxEmojis         The max amount of emojis able to be parsed.
	 * @param allowCustomEmotes Should custom emojis be able to be parsed.
	 * @return An instance that parses multiple emojis.
	 */
	public static EmojiArgument.Multiple getMultipleMax(int maxEmojis, boolean allowCustomEmotes) {
		return new EmojiArgument.Multiple(0, maxEmojis, allowCustomEmotes);
	}

	@Override
	public ParsedArgument<Activity.Emoji> parse(MessageReader reader) throws CommandSyntaxException {
		String string = reader.nextArgument();
		List<Activity.Emoji> emojiCount = getEmojis(string);
		if (emojiCount.size() == 1) {
			return ParsedArgument.parse(new Activity.Emoji(string));
		} else {
			Matcher matcher = CUSTOM_EMOJI_PATTERN.matcher(string);
			if (matcher.find()) {
				if (this.allowCustomEmotes) {
					throw reader.getExceptionProvider().getCustomEmojiException().create();
				}
				try {
					return ParsedArgument.parse(new Activity.Emoji(matcher.group(2), Long.parseLong(matcher.group(3)), !matcher.group(1).isEmpty()));
				} catch (NumberFormatException e) {
					throw reader.getExceptionProvider().getInvalidEmojiException().create(string);
				}
			}
		}
		throw reader.getExceptionProvider().getInvalidEmojiException().create(string);
	}

	/**
	 * Will be used for multiple emoji parsing support Later.
	 */
	private static List<Activity.Emoji> getEmojis(String string) {
		byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
		String convertedString = new String(utf8Bytes, StandardCharsets.UTF_8);
		Matcher matcher = UNICODE_EMOJI_PATTERN.matcher(convertedString);
		List<Activity.Emoji> matchList = new ArrayList<>();
		while (matcher.find()) {
			matchList.add(new Activity.Emoji(matcher.group()));
		}
		return matchList;
	}

	public static class Multiple implements Argument<List<Activity.Emoji>> {
		private final int min, max;
		private final boolean allowCustomEmotes;

		private Multiple(int min, int max, boolean allowCustomEmotes) {
			this.min = Math.max(0, min);
			this.max = max;
			this.allowCustomEmotes = allowCustomEmotes;
		}

		@Override
		public ParsedArgument<List<Activity.Emoji>> parse(MessageReader reader) throws CommandSyntaxException {
			String string = reader.nextArgument();
			List<Activity.Emoji> emojis = new ArrayList<>(getEmojis(string));
			Matcher matcher = CUSTOM_EMOJI_PATTERN.matcher(string);
			if (matcher.find()) {
				if (!this.allowCustomEmotes) {
					throw reader.getExceptionProvider().getCustomEmojiException().create();
				}
				emojis.add(new Activity.Emoji(matcher.group(2), Long.parseLong(matcher.group(3)), !matcher.group(1).isEmpty()));
				while (matcher.find()) {
					try {
						emojis.add(new Activity.Emoji(matcher.group(2), Long.parseLong(matcher.group(3)), !matcher.group(1).isEmpty()));
					} catch (NumberFormatException e) {
						throw reader.getExceptionProvider().getInvalidEmojiException().create(matcher.group());
					}
				}
			}
			int size = emojis.size();
			if (size > 0) {
				if (size > this.max) {
					throw reader.getExceptionProvider().getTooManyEmojisException().create(string, this.max);
				} else if (size < this.min) {
					throw reader.getExceptionProvider().getNotEnoughEmojisException().create(string, this.min);
				}
				return ParsedArgument.parse(emojis);
			}
			throw reader.getExceptionProvider().getNoValidEmojisException().create(string);
		}
	}
}
