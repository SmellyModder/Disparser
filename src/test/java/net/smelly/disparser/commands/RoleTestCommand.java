package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.jda.RoleArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.CommandNode;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FormattedCommandMessage;

public final class RoleTestCommand extends Command<GuildMessageReceivedEvent, GuildMessageCommandContext> {
	private static final RootNode<GuildMessageReceivedEvent, GuildMessageCommandContext> NODE = RootNode.Builder.create(GuildMessageCommandContext.class)
		.then(
			CommandNode.Builder.named(channel -> "add", GuildMessageCommandContext.class)
				.then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(UserArgument.get(), channel -> "user"), GuildMessageCommandContext.class)
						.then(ArgumentNode.Builder.create(ConfiguredArgument.named(RoleArgument.get(), channel -> "role"), GuildMessageCommandContext.class)
							.consumes(context -> {
								processCommand(true, context.getGuild(), context.getParsedResult(1), context.getParsedResult(0), context.getFeedbackHandler());
							})
						)
				).then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(RoleArgument.get(), channel -> "role"), GuildMessageCommandContext.class).consumes(context -> {
						processCommand(true, context.getGuild(), context.getParsedResult(0), context.getAuthor(), context.getFeedbackHandler());
					})
				)
		).then(
			CommandNode.Builder.named(channel -> "remove", GuildMessageCommandContext.class)
				.then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(UserArgument.get(), channel -> "user"), GuildMessageCommandContext.class)
						.then(ArgumentNode.Builder.create(ConfiguredArgument.named(RoleArgument.get(), channel -> "role"), GuildMessageCommandContext.class)
								.consumes(context -> {
									processCommand(false, context.getGuild(), context.getParsedResult(1), context.getParsedResult(0), context.getFeedbackHandler());
								})
						)
				).then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(RoleArgument.get(), channel -> "role"), GuildMessageCommandContext.class)
							.consumes(context -> {
								processCommand(false, context.getGuild(), context.getParsedResult(0), context.getAuthor(), context.getFeedbackHandler());
							})
				)
		).build();

	public RoleTestCommand() {
		super("role", NODE);
	}

	private static void processCommand(boolean add, Guild guild, Role role, User user, FeedbackHandler feedbackHandler) {
		if (add) {
			guild.addRoleToMember(user.getIdLong(), role).queue(aVoid -> {
				feedbackHandler.sendFeedback(new FormattedCommandMessage("Successfully added role `%1$s` to %2$s!", role.getName(), user.getName()));
			});
		} else {
			guild.removeRoleFromMember(user.getIdLong(), role).queue(aVoid -> {
				feedbackHandler.sendFeedback(new FormattedCommandMessage("Successfully removed role `%1$s` from %2$s!", role.getName(), user.getName()));
			});
		}
	}
}
