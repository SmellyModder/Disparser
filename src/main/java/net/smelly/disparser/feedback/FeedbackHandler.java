package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This interface is used for sending feedback when a command is processed.
 * <p>Implement this on types that will be used for sending command feedback.</p>
 *
 * @author Luke Tonon
 * @see SimpleFeedbackHandler
 * @see FeedbackHandlerBuilder#SIMPLE_BUILDER
 */
@ThreadSafe
public interface FeedbackHandler {
	/**
	 * Sends a {@link CommandMessage}.
	 *
	 * @param commandMessage The {@link CommandMessage} to send.
	 */
	void sendFeedback(CommandMessage commandMessage);

	/**
	 * Sends a {@link MessageEmbed}.
	 *
	 * @param messageEmbed The {@link MessageEmbed} to send.
	 */
	void sendFeedback(MessageEmbed messageEmbed);

	/**
	 * Sends a success message.
	 * This is equivalent to {@link #sendFeedback(CommandMessage)} with the difference being this should include a 'success' detail with the message.
	 *
	 * @param message The message to be used for the success message.
	 * @see SimpleFeedbackHandler#sendSuccess(CommandMessage).
	 * @see net.smelly.disparser.util.MessageUtil#createSuccessfulMessage(String).
	 */
	void sendSuccess(CommandMessage message);

	/**
	 * Sends an {@link Exception} as an error.
	 *
	 * @param exception The exception to send the error message for.
	 * @see SimpleFeedbackHandler#sendSuccess(CommandMessage).
	 * @see net.smelly.disparser.util.MessageUtil#createErrorMessage(String).
	 */
	void sendError(Exception exception);
}
