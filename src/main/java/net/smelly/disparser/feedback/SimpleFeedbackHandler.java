package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.smelly.disparser.util.MessageUtil;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple implementation class of {@link FeedbackHandler}.
 *
 * @author Luke Tonon
 * @see FeedbackHandler
 */
@ThreadSafe
public class SimpleFeedbackHandler implements FeedbackHandler {
	private final MessageChannel channel;

	/**
	 * Constructs a new {@link SimpleFeedbackHandler} with a {@link MessageChannel} from a {@link CommandContext}.
	 *
	 * @param channel {@link MessageChannel} to build the {@link FeedbackHandler}.
	 */
	public SimpleFeedbackHandler(MessageChannel channel) {
		this.channel = channel;
	}

	/**
	 * Sends a {@link CommandMessage} to the {@link MessageChannel} belonging to this instance.
	 *
	 * @param commandMessage The {@link CommandMessage} to send.
	 */
	@Override
	public void sendFeedback(CommandMessage commandMessage) {
		this.channel.sendMessage(commandMessage.getMessage(this.channel)).queue();
	}

	/**
	 * Sends a {@link MessageEmbed} to the {@link MessageChannel} belonging to this instance.
	 *
	 * @param messageEmbed The {@link MessageEmbed} to send.
	 */
	@Override
	public void sendFeedback(MessageEmbed messageEmbed) {
		this.channel.sendMessage(messageEmbed).queue();
	}

	/**
	 * Sends a {@link MessageEmbed} created from {@link MessageUtil#createSuccessfulMessage(String)} using the supplied message string.
	 *
	 * @param message The message to be used for the success message.
	 */
	@Override
	public void sendSuccess(CommandMessage message) {
		this.sendFeedback(MessageUtil.createSuccessfulMessage(message.getMessage(this.channel)));
	}

	/**
	 * Sends a {@link MessageEmbed} created from {@link MessageUtil#createErrorMessage(String)} using the passed in exception's {@link Exception#getMessage()}.
	 * <p> If the exception has no message then it will use "Unknown" as the cause message. </p>
	 *
	 * @param exception The exception to send the error message for.
	 */
	@Override
	public void sendError(Exception exception) {
		String message = exception.getMessage();
		this.sendFeedback(MessageUtil.createErrorMessage(message != null ? message : "Unknown"));
	}
}
