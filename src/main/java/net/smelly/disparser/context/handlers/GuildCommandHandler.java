package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProviderBuilder;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import java.util.Optional;
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

	public GuildCommandHandler(CommandPropertyMap<GuildMessageCommandContext> commandPropertyMap, Function<GuildMessageReceivedEvent, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder, ExecutorService executorService) {
		super(commandPropertyMap, prefixFunction, feedbackHandlerBuilder, exceptionProviderBuilder, executorService);
	}

	@Override
	public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
		this.executorService.execute(() -> {
			String firstComponent = event.getMessage().getContentRaw().split(" ")[0];
			String prefix = this.getPrefix(event);
			if (firstComponent.startsWith(prefix)) {
				Command<GuildMessageCommandContext> command = this.aliasMap.get(firstComponent.substring(prefix.length()));
				if (command != null) {
					Optional<GuildMessageCommandContext> commandContext = GuildMessageCommandContext.create(event, command, this.getPermissions(command), this.feedbackHandlerBuilder, this.exceptionProviderBuilder);
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
			GuildCommandHandler commandHandler = new GuildCommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.exceptionProviderBuilder, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}

}
