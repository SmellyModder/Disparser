package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.CommandContextBuilder;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.properties.CommandPropertyMap;
import net.smelly.disparser.util.MessageUtil;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * A {@link AbstractCommandHandler} extension for handling commands for the {@link GuildMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see AbstractCommandHandler
 * @see GuildMessageReceivedEvent
 */
public class GuildCommandHandler extends AbstractCommandHandler<GuildMessageReceivedEvent, GuildMessageCommandContext> {

	public GuildCommandHandler(CommandPropertyMap<GuildMessageReceivedEvent, GuildMessageCommandContext> commandPropertyMap, Function<GuildMessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderFunction, executorService);
	}

	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
		if (!this.executorService.isShutdown()) {
			this.executorService.execute(() -> {
				String firstComponent = MessageUtil.getFirstComponent(event.getMessage().getContentRaw());
				String prefix = this.getPrefix(event);
				if (firstComponent.startsWith(prefix)) {
					Command<GuildMessageReceivedEvent, GuildMessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
					if (command != null) {
						Member member = event.getMember();
						MessageChannel channel = event.getChannel();
						FeedbackHandler feedbackHandler = this.feedbackHandlerBuilder.build(channel);
						BuiltInExceptionProvider provider = this.exceptionProviderFunction.apply(channel);
						UnmodifiableSet<Permission> permissions = this.getPermissions(command);
						if (member != null && !member.hasPermission(permissions)) {
							feedbackHandler.sendError(provider.getMissingPermissionsException().create(permissions));
						} else {
							GuildMessageCommandContext.Builder builder = (GuildMessageCommandContext.Builder) CommandContextBuilder.disparseRoot(new GuildMessageCommandContext.Builder(event, this.commandPropertyMap.getPropertyMap(command), channel, feedbackHandler, provider, MessageReader.create(provider, event.getMessage())), command.getRootNode());
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

	public static class Builder extends AbstractCommandHandlerBuilder<GuildMessageReceivedEvent, GuildMessageCommandContext, GuildCommandHandler, Builder> {
		/**
		 * Sets a prefix for the {@link GuildCommandHandler} using {@link Guild} as the function parameter.
		 *
		 * @param prefixFunction The prefix function to set.
		 * @return This builder.
		 */
		public Builder setGuildPrefix(Function<Guild, String> prefixFunction) {
			this.prefixFunction = (event) -> prefixFunction.apply(event.getGuild());
			return this;
		}

		/**
		 * @return The {@link GuildMessageCommandContext} class for the type of {@link CommandContext} for the type of {@link AbstractCommandHandler} this builds for.
		 */
		@Override
		public Class<GuildMessageCommandContext> getContextClass() {
			return GuildMessageCommandContext.class;
		}

		/**
		 * Builds this builder.
		 *
		 * @return The constructed {@link GuildCommandHandler} from this builder.
		 */
		@Override
		public GuildCommandHandler build() {
			GuildCommandHandler commandHandler = new GuildCommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderFunction, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}

}
