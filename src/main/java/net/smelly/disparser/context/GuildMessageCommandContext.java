package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * An extension of {@link CommandContext} for the {@link GuildMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class GuildMessageCommandContext extends CommandContext<GuildMessageReceivedEvent> {

	public GuildMessageCommandContext(GuildMessageReceivedEvent event, Map<Integer, ParsedArgument<?>> parsedArguments, CommandPropertyMap.PropertyMap propertyMap, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, propertyMap, feedbackHandler, exceptionProvider);
	}

	/**
	 * Equivalent to {@link GuildMessageReceivedEvent#getGuild()}.
	 *
	 * @return The {@link Guild} that the {@link GuildMessageReceivedEvent} was sent in.
	 */
	@Nonnull
	public Guild getGuild() {
		return this.event.getGuild();
	}

	/**
	 * Equivalent to {@link GuildMessageReceivedEvent#getChannel()}.
	 *
	 * @return The {@link TextChannel} that the {@link GuildMessageReceivedEvent} was sent in.
	 */
	@Nonnull
	public TextChannel getChannel() {
		return this.event.getChannel();
	}

	/**
	 * Equivalent to {@link GuildMessageReceivedEvent#getMember()}.
	 *
	 * @return The {@link Member} that sent the {@link GuildMessageReceivedEvent}.
	 */
	@Nullable
	public Member getMember() {
		return this.event.getMember();
	}

	/**
	 * Equivalent to {@link GuildMessageReceivedEvent#getAuthor()}.
	 *
	 * @return The {@link User} that sent the {@link GuildMessageReceivedEvent}.
	 */
	@Nonnull
	public User getAuthor() {
		return this.event.getAuthor();
	}

	/**
	 * Equivalent to {@link GuildMessageReceivedEvent#isWebhookMessage()}.
	 *
	 * @return True if the source of this {@link GuildMessageReceivedEvent} is from a webhook.
	 */
	public boolean isWebhookMessage() {
		return this.event.isWebhookMessage();
	}

	public static class Builder extends CommandContextBuilder<GuildMessageReceivedEvent, GuildMessageCommandContext> {

		public Builder(GuildMessageReceivedEvent event, CommandPropertyMap.PropertyMap propertyMap, MessageChannel channel, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider, MessageReader reader) {
			super(event, propertyMap, channel, feedbackHandler, exceptionProvider, reader);
		}

		@Override
		public GuildMessageCommandContext build() {
			return new GuildMessageCommandContext(this.getEvent(), this.getArguments(), this.getPropertyMap(), this.getFeedbackHandler(), this.getExceptionProvider());
		}

	}
}
