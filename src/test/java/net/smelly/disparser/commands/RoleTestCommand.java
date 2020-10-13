package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.arguments.jda.RoleArgument;
import net.smelly.disparser.arguments.jda.UserArgument;

public final class RoleTestCommand extends Command {

	public RoleTestCommand() {
		super("role", EnumArgument.get(Action.class), UserArgument.get().asOptional(), RoleArgument.get());
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		GuildMessageReceivedEvent event = context.getEvent();
		User user = context.getParsedResultOrElse(1, event.getAuthor());
		Guild guild = event.getGuild();
		Role role = context.getParsedResult(2);
		Action action = context.getParsedResult(0);
		(action == Action.ADD ? guild.addRoleToMember(user.getIdLong(), role) : guild.removeRoleFromMember(user.getIdLong(), role)).queue();
		context.getFeedbackHandler().sendSuccess(String.format(action.message, role.getName(), user.getName()));
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
