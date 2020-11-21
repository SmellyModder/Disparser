package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.CommandContextBuilder;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.properties.CommandPropertyMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nonnull;
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

	public CommandHandler(CommandPropertyMap<MessageReceivedEvent, MessageCommandContext> commandPropertyMap, Function<MessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderFunction, executorService);
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if (!this.executorService.isShutdown()) {
			this.executorService.execute(() -> {
				String firstComponent = event.getMessage().getContentRaw().split(" ")[0];
				String prefix = this.getPrefix(event);
				if (firstComponent.startsWith(prefix)) {
					Command<MessageReceivedEvent, MessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
					if (command != null) {
						MessageChannel channel = event.getChannel();
						FeedbackHandler feedbackHandler = this.feedbackHandlerBuilder.build(channel);
						BuiltInExceptionProvider provider = this.exceptionProviderFunction.apply(channel);
						Member member = event.getMember();
						UnmodifiableSet<Permission> permissions = this.getPermissions(command);
						if (member != null && !member.hasPermission(permissions)) {
							feedbackHandler.sendError(provider.getMissingPermissionsException().create(permissions));
						} else {
							MessageCommandContext.Builder builder = (MessageCommandContext.Builder) CommandContextBuilder.disparseRoot(new MessageCommandContext.Builder(event, this.commandPropertyMap.getPropertyMap(command), channel, feedbackHandler, provider, MessageReader.create(provider, event.getMessage())), command.getRootNode());
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
				}
			});
		}
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
			CommandHandler commandHandler = new CommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderFunction, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}

}
