package disparser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collection;

/**
 * A class that holds some useful message functions, all of which are used in Disparser.
 * 
 * @author Luke Tonon
 */
public final class MessageUtil {
	
	public static MessageEmbed createErrorMessage(String errorMessage) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(":x: " + "Command Failed");
		embedBuilder.appendDescription("**Reason: **" + errorMessage);
		embedBuilder.setColor(14495300);
		return embedBuilder.build();
	}
	
	public static MessageEmbed createSuccessfulMessage(String message) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle(":white_check_mark: " + "Command Successful");
		embedBuilder.appendDescription(message);
		embedBuilder.setColor(7844437);
		return embedBuilder.build();
	}
	
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
	
	public static String createFormattedSentenceOfCollection(Collection<?> collection) {
		StringBuilder builder = new StringBuilder();
		int size = collection.size();
		for (int i = 0; i < size; i++) {
			builder.append(collection.toArray()[i]).append(i == size - 2 ? (size > 2 ? ", and " : " and ") : i == size - 1 ? "" : ", ");
		}
		return builder.toString();
	}
	
}