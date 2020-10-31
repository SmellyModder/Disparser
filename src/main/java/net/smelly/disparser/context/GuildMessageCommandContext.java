package net.smelly.disparser.context;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
 * An extension of {@link CommandContext} for the {@link GuildMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class GuildMessageCommandContext extends CommandContext<GuildMessageReceivedEvent> {

	public GuildMessageCommandContext(GuildMessageReceivedEvent event, List<ParsedArgument<?>> parsedArguments, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, feedbackHandler, exceptionProvider);
	}

	/**
	 * Attempts to create a {@link GuildMessageCommandContext} for a {@link GuildMessageReceivedEvent} and {@link Command}.
	 * <p>If an error occurs trying to create the {@link GuildMessageCommandContext} then {@link Optional#empty()} will be returned.</p>
	 *
	 * @param event                    A {@link GuildMessageReceivedEvent} to create this {@link GuildMessageCommandContext} for.
	 * @param command                  A {@link Command} to create this {@link GuildMessageCommandContext} for.
	 * @param permissions              A {@link Set} of {@link Permission}s to test on the author of the event.
	 * @param feedbackHandlerBuilder   A {@link FeedbackHandlerBuilder} to construct a new {@link FeedbackHandler}.
	 * @param exceptionProviderBuilder A {@link BuiltInExceptionProviderBuilder} to construct a new {@link BuiltInExceptionProvider}.
	 * @return {@link Optional#empty()} if an error occurred trying to create the {@link GuildMessageCommandContext}, otherwise an {@link Optional} containing the created {@link GuildMessageCommandContext}.
	 */
	public static Optional<GuildMessageCommandContext> create(GuildMessageReceivedEvent event, Command<GuildMessageCommandContext> command, Set<Permission> permissions, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder) {
		GuildMessageCommandContext commandContext = new GuildMessageCommandContext(event, new ArrayList<>(), feedbackHandlerBuilder.build(event.getChannel()), exceptionProviderBuilder.build(event.getChannel()));

		Member member = event.getMember();
		if (member == null) return Optional.empty();

		BuiltInExceptionProvider builtInExceptionProvider = commandContext.getExceptionProvider();
		if (!member.hasPermission(permissions)) {
			commandContext.getFeedbackHandler().sendError(builtInExceptionProvider.getMissingPermissionsException().create(permissions));
			return Optional.empty();
		}

		return CommandContext.disparseArguments(commandContext, event.getMessage(), command.getArguments());
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

}
