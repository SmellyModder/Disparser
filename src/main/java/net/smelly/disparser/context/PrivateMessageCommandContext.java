package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An extension of {@link CommandContext} for the {@link PrivateMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class PrivateMessageCommandContext extends CommandContext<PrivateMessageReceivedEvent> {

	public PrivateMessageCommandContext(PrivateMessageReceivedEvent event, List<ParsedArgument<?>> parsedArguments, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, feedbackHandler, exceptionProvider);
	}

	/**
	 * Attempts to create a {@link PrivateMessageCommandContext} for a {@link PrivateMessageCommandContext} and {@link Command}.
	 * <p>If an error occurs trying to create the {@link GuildMessageCommandContext} then {@link Optional#empty()} will be returned.</p>
	 *
	 * @param event                  A {@link PrivateMessageCommandContext} to create this {@link PrivateMessageCommandContext} for.
	 * @param command                A {@link Command} to create this {@link PrivateMessageCommandContext} for.
	 * @param feedbackHandlerBuilder A {@link FeedbackHandlerBuilder} to construct a new {@link FeedbackHandler}.
	 * @param exceptionProvider      A {@link BuiltInExceptionProvider} to get built-in exception creators from.
	 * @return {@link Optional#empty()} if an error occurred trying to create the {@link PrivateMessageCommandContext}, otherwise an {@link Optional} containing the created {@link PrivateMessageCommandContext}.
	 */
	public static Optional<PrivateMessageCommandContext> create(PrivateMessageReceivedEvent event, Command<PrivateMessageCommandContext> command, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProvider exceptionProvider) {
		PrivateChannel channel = event.getChannel();
		return CommandContext.disparseArguments(new PrivateMessageCommandContext(event, new ArrayList<>(), feedbackHandlerBuilder.build(channel), exceptionProvider), event.getMessage(), command.getArguments());
	}

}
