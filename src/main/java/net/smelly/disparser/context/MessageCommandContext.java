package net.smelly.disparser.context;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProviderBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * An extension of {@link CommandContext} for the {@link MessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class MessageCommandContext extends CommandContext<MessageReceivedEvent> {

	public MessageCommandContext(MessageReceivedEvent event, List<ParsedArgument<?>> parsedArguments, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, feedbackHandler, exceptionProvider);
	}

	/**
	 * Attempts to create a {@link MessageCommandContext} for a {@link MessageReceivedEvent} and {@link Command}.
	 * <p>If an error occurs trying to create the {@link MessageCommandContext} then {@link Optional#empty()} will be returned.</p>
	 *
	 * @param event                    A {@link MessageReceivedEvent} to create this {@link MessageCommandContext} for.
	 * @param command                  A {@link Command} to create this {@link MessageCommandContext} for.
	 * @param permissions              A {@link Set} of {@link Permission}s to test on the author of the event.
	 * @param feedbackHandlerBuilder   A {@link FeedbackHandlerBuilder} to construct a new {@link FeedbackHandler}.
	 * @param exceptionProviderBuilder A {@link BuiltInExceptionProviderBuilder} to construct a new {@link BuiltInExceptionProvider}.
	 * @return {@link Optional#empty()} if an error occurred trying to create the {@link MessageCommandContext}, otherwise an {@link Optional} containing the created {@link MessageCommandContext}.
	 */
	public static Optional<MessageCommandContext> create(MessageReceivedEvent event, Command<MessageCommandContext> command, Set<Permission> permissions, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder) {
		MessageCommandContext commandContext = new MessageCommandContext(event, new ArrayList<>(), feedbackHandlerBuilder.build(event.getChannel()), exceptionProviderBuilder.build(event.getChannel()));

		if (commandContext.isFromGuild()) {
			Member member = event.getMember();
			if (member == null) return Optional.empty();

			BuiltInExceptionProvider builtInExceptionProvider = commandContext.getExceptionProvider();
			if (!member.hasPermission(permissions)) {
				commandContext.getFeedbackHandler().sendError(builtInExceptionProvider.getMissingPermissionsException().create(permissions));
				return Optional.empty();
			}
		}

		return CommandContext.disparseArguments(commandContext, event.getMessage(), command.getArguments());
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

}
