package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.PrivateMessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProviderBuilder;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * A {@link AbstractCommandHandler} extension for handling commands for the {@link PrivateMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see AbstractCommandHandler
 * @see PrivateMessageReceivedEvent
 */
public class PrivateCommandHandler extends AbstractCommandHandler<PrivateMessageReceivedEvent, PrivateMessageCommandContext> {

	public PrivateCommandHandler(CommandPropertyMap<PrivateMessageCommandContext> commandPropertyMap, Function<PrivateMessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderBuilder, executorService);
	}

	@Override
	public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
		if (!this.executorService.isShutdown()) {
			this.executorService.execute(() -> {
				String firstComponent = event.getMessage().getContentRaw().split(" ")[0];
				String prefix = this.getPrefix(event);
				if (firstComponent.startsWith(prefix)) {
					Command<PrivateMessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
					if (command != null) {
						Optional<PrivateMessageCommandContext> commandContext = PrivateMessageCommandContext.create(event, command, this.feedbackHandlerBuilder, this.exceptionProviderBuilder);
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
	}

	public static class Builder extends AbstractCommandHandlerBuilder<PrivateMessageReceivedEvent, PrivateMessageCommandContext, PrivateCommandHandler, Builder> {
		/**
		 * Sets a prefix for the {@link PrivateCommandHandler} using the {@link User} that's the author of the event as the function parameter.
		 *
		 * @param prefixFunction The prefix function to set.
		 * @return This builder.
		 */
		public Builder setAuthorPrefix(Function<User, String> prefixFunction) {
			this.prefixFunction = (event) -> prefixFunction.apply(event.getAuthor());
			return this;
		}

		/**
		 * @return The {@link PrivateMessageCommandContext} class for the type of {@link CommandContext} for the type of {@link AbstractCommandHandler} this builds for.
		 */
		@Override
		public Class<PrivateMessageCommandContext> getContextClass() {
			return PrivateMessageCommandContext.class;
		}

		/**
		 * Builds this builder.
		 *
		 * @return The constructed {@link PrivateCommandHandler} from this builder.
		 */
		@Override
		public PrivateCommandHandler build() {
			PrivateCommandHandler commandHandler = new PrivateCommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderBuilder, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}

}
