package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProviderBuilder;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * A {@link AbstractCommandHandler} extension for handling commands for the {@link MessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see AbstractCommandHandler
 * @see MessageReceivedEvent
 */
public class CommandHandler extends AbstractCommandHandler<MessageReceivedEvent, MessageCommandContext> {

	public CommandHandler(CommandPropertyMap<MessageCommandContext> commandPropertyMap, Function<MessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderBuilder, executorService);
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		this.executorService.execute(() -> {
			String firstComponent = event.getMessage().getContentRaw().split(" ")[0];
			String prefix = this.getPrefix(event);
			if (firstComponent.startsWith(prefix)) {
				Command<MessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
				if (command != null) {
					Optional<MessageCommandContext> commandContext = MessageCommandContext.create(event, command, this.getPermissions(command), this.feedbackHandlerBuilder, this.exceptionProviderBuilder);
					commandContext.ifPresent(context -> {
						try {
							command.processCommand(context);
						} catch (Exception exception) {
							context.getFeedbackHandler().sendError(exception);
							exception.printStackTrace();
						}
					});
				}
			}
		});
	}

	/**
	 * Builder class extension of {@link AbstractCommandHandler} for {@link CommandHandler}.
	 */
	public static class Builder extends AbstractCommandHandlerBuilder<MessageReceivedEvent, MessageCommandContext, CommandHandler, Builder> {

		/**
		 * @return The {@link MessageCommandContext} class for the type of {@link CommandContext} for the type of {@link AbstractCommandHandler} this builds for.
		 */
		@Override
		public Class<MessageCommandContext> getContextClass() {
			return MessageCommandContext.class;
		}

		/**
		 * Builds this builder.
		 *
		 * @return The constructed {@link CommandHandler} from this builder.
		 */
		@Override
		public CommandHandler build() {
			CommandHandler commandHandler = new CommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderBuilder, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}

	}

}
