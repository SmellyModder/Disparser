package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.arguments.jda.RoleArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.FormattedCommandMessage;

public final class RoleTestCommand extends Command<GuildMessageCommandContext> {

	public RoleTestCommand() {
		super("role", ConfiguredArgument.named(EnumArgument.get(Action.class), channel -> "action"), UserArgument.get().withConfiguration(channel -> "user", CommandMessage.EMPTY, true), ConfiguredArgument.named(RoleArgument.get(), channel -> "role"));
	}

	@Override
	public void processCommand(GuildMessageCommandContext context) throws Exception {
		User user = context.getParsedResultOrElse(1, context.getAuthor());
		Guild guild = context.getGuild();
		Role role = context.getParsedResult(2);
		Action action = context.getParsedResult(0);
		(action == Action.ADD ? guild.addRoleToMember(user.getIdLong(), role) : guild.removeRoleFromMember(user.getIdLong(), role)).queue();
		context.getFeedbackHandler().sendSuccess(new FormattedCommandMessage(action.message, role.getName(), user.getName()));
	}

	enum Action {
		ADD("Successfully added role `%1$s` to %2$s!"),
		REMOVE("Successfully removed role `%1$s` from %2$s!");

		private final String message;

		Action(String message) {
			this.message = message;
		}
	}

}
