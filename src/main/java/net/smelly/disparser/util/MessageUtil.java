package net.smelly.disparser.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collection;

/**
 * A class that holds some useful message functions, all of which are used in Disparser.
 *
 * @author Luke Tonon
 */
public final class MessageUtil {
	/**
	 * Creates a simple error message as a {@link MessageEmbed}.
	 *
	 * @param message The error reason message.
	 * @return a simple error message as a {@link MessageEmbed}.
	 * @see <a href="https://cdn.discordapp.com/attachments/667088262287851551/748744108939411496/errored_test.PNG">Errored Message</a>
	 */
	public static MessageEmbed createErrorMessage(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(":x: " + "Command Failed");
		embedBuilder.appendDescription("**Reason: **" + message);
		embedBuilder.setColor(14495300);
		return embedBuilder.build();
	}

	/**
	 * Creates a simple successful completion message as a {@link MessageEmbed}.
	 *
	 * @param message The message to be displayed.
	 * @return a simple successful completion message as a {@link MessageEmbed}.
	 * @see <a href="https://cdn.discordapp.com/attachments/667088262287851551/748744125808902145/sucessful_test.PNG">Sucessful Message</a>
	 */
	public static MessageEmbed createSuccessfulMessage(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(":white_check_mark: " + "Command Successful");
		embedBuilder.appendDescription(message);
		embedBuilder.setColor(7844437);
		return embedBuilder.build();
	}

	/**
	 * Gets the ordinal for an integer.
	 * 1st, 2nd, 3rd, 4th, etc...
	 *
	 * @param value The integer.
	 * @return the ordinal for an integer.
	 */
	public static String getOrdinalForInteger(int value) {
		int hunRem = value % 100;
		int tenRem = value % 10;
		if (hunRem - tenRem == 10) return "th";
		switch (tenRem) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	/**
	 * Creates an English formatted sentence of a collection.
	 *
	 * @param collection - The collection to format.
	 * @return an English formatted sentence of a collection.
	 */
	public static String createFormattedSentenceOfCollection(Collection<?> collection) {
		StringBuilder builder = new StringBuilder();
		int size = collection.size();
		for (int i = 0; i < size; i++) {
			builder.append(collection.toArray()[i]).append(i == size - 2 ? (size > 2 ? ", and " : " and ") : i == size - 1 ? "" : ", ");
		}
		return builder.toString();
	}

	/**
	 * Searches for the first part of a string. (i.e. All the characters until a whitespace is found)
	 * <p>This is useful for getting the first part of a message.</p>
	 *
	 * @param message A string to get the first part of.
	 * @return The first part of the string.
	 */
	public static String getFirstComponent(String message) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			char character = message.charAt(i);
			if (!Character.isWhitespace(character)) {
				builder.append(character);
			} else break;
		}
		return builder.toString();
	}
}