package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.User;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.MessageCommandContext;

public final class UserProfileTestCommand extends Command<MessageCommandContext> {

	public UserProfileTestCommand() {
		super("user", ConfiguredArgument.named(UserArgument.get(), channel -> "user"));
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		User user = context.getParsedResult(0);
		String url = user.getAvatarUrl();
		context.getEvent().getChannel().sendMessage(url == null ? "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png" : url).queue();
	}

}