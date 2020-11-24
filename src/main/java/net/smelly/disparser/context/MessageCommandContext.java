package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An extension of {@link CommandContext} for the {@link MessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class MessageCommandContext extends CommandContext<MessageReceivedEvent> {

	public MessageCommandContext(MessageReceivedEvent event, List<ParsedArgument<?>> parsedArguments, CommandPropertyMap.PropertyMap propertyMap, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, propertyMap, feedbackHandler, exceptionProvider);
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getChannel()}.
	 *
	 * @return The {@link MessageChannel} that the {@link MessageReceivedEvent} was created from.
	 */
	@Nonnull
	public MessageChannel getChannel() {
		return this.event.getChannel();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getMessage()}.
	 *
	 * @return The {@link Message} that was sent in the {@link MessageReceivedEvent}.
	 */
	@Nonnull
	public Message getMessage() {
		return this.event.getMessage();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getAuthor()}.
	 *
	 * @return The {@link User} that sent the {@link MessageReceivedEvent}.
	 */
	@Nonnull
	public User getAuthor() {
		return this.event.getAuthor();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getGuild()}.
	 *
	 * @return The {@link Guild} that the {@link MessageReceivedEvent} was created from.
	 * @throws java.lang.IllegalStateException If this was not sent in a {@link TextChannel}.
	 */
	@Nonnull
	public Guild getGuild() {
		return this.event.getGuild();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getGuild()}.
	 *
	 * @return The {@link TextChannel} that the {@link MessageReceivedEvent} was created from.
	 * @throws java.lang.IllegalStateException If this was not sent in a {@link TextChannel}.
	 */
	@Nonnull
	public TextChannel getTextChannel() {
		return this.event.getTextChannel();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#getMember()}.
	 *
	 * @return The {@link TextChannel} that the {@link MessageReceivedEvent} was created from.
	 * @throws java.lang.UnsupportedOperationException If this is not a Received Message from {@link net.dv8tion.jda.api.entities.MessageType#DEFAULT}
	 */
	@Nullable
	public Member getMember() {
		return this.event.getMember();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#isFromGuild()}.
	 *
	 * @return True if the source of this {@link MessageReceivedEvent} is from a {@link Guild}.
	 */
	public boolean isFromGuild() {
		return this.event.isFromGuild();
	}

	/**
	 * Equivalent to {@link MessageReceivedEvent#isWebhookMessage()}.
	 *
	 * @return True if the source of this {@link MessageReceivedEvent} is from a webhook.
	 */
	public boolean isWebhookMessage() {
		return this.event.isWebhookMessage();
	}

	public static class Builder extends CommandContextBuilder<MessageReceivedEvent, MessageCommandContext> {

		public Builder(MessageReceivedEvent event, CommandPropertyMap.PropertyMap propertyMap, MessageChannel channel, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider, MessageReader reader) {
			super(event, propertyMap, channel, feedbackHandler, exceptionProvider, reader);
		}

		@Override
		public MessageCommandContext build() {
			return new MessageCommandContext(this.getEvent(), this.getArguments(), this.getPropertyMap(), this.getFeedbackHandler(), this.getExceptionProvider());
		}

	}
}
