package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.CommandContextBuilder;
import net.smelly.disparser.context.PrivateMessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.properties.CommandPropertyMap;
import net.smelly.disparser.util.MessageUtil;

import javax.annotation.Nonnull;
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

	public PrivateCommandHandler(CommandPropertyMap<PrivateMessageReceivedEvent, PrivateMessageCommandContext> commandPropertyMap, Function<PrivateMessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderFunction, executorService);
	}

	@Override
	public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
		if (!this.executorService.isShutdown()) {
			this.executorService.execute(() -> {
				String firstComponent = MessageUtil.getFirstComponent(event.getMessage().getContentRaw());
				String prefix = this.getPrefix(event);
				if (firstComponent.startsWith(prefix)) {
					Command<PrivateMessageReceivedEvent, PrivateMessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
					if (command != null) {
						MessageChannel channel = event.getChannel();
						FeedbackHandler feedbackHandler = this.feedbackHandlerBuilder.build(channel);
						BuiltInExceptionProvider provider = this.exceptionProviderFunction.apply(channel);
						PrivateMessageCommandContext.Builder builder = (PrivateMessageCommandContext.Builder) CommandContextBuilder.disparseRoot(new PrivateMessageCommandContext.Builder(event, this.commandPropertyMap.getPropertyMap(command), channel, feedbackHandler, provider, MessageReader.create(provider, event.getMessage())), command.getRootNode());
						if (builder.getException() != null) {
							feedbackHandler.sendError(builder.getException());
						} else if (builder.getConsumer() != null) {
							try {
								command.processCommand(builder.build(), builder.getConsumer());
							} catch (CommandException commandException) {
								feedbackHandler.sendError(commandException);
							} catch (Exception exception) {
								exception.printStackTrace();
								feedbackHandler.sendError(exception);
							}
						}
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
			PrivateCommandHandler commandHandler = new PrivateCommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderFunction, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}

}
