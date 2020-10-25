package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An interface representing the string value of a message sent by a command. Useful for implementing localized messages.
 * <p>{@link #getMessage(TextChannel)} contains a {@link TextChannel} parameter to allow localization based on a {@link TextChannel}, this allows for guild-based localization as well.</p>
 *
 * @author Luke Tonon
 */
@ThreadSafe
@FunctionalInterface
public interface CommandMessage {
	/**
	 * @param channel The channel to get this message for, can be null.
	 * @return The string value of this message.
	 */
	String getMessage(@Nullable TextChannel channel);
}
