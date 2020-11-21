package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

public final class UserProfileTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(UserArgument.get(), channel -> "user"), MessageCommandContext.class)
				.consumes(context -> UserProfileTestCommand.processUser(context.getChannel(), context.getParsedResult(0)))
		).consumes(context -> {
			UserProfileTestCommand.processUser(context.getChannel(), context.getAuthor());
		}).build();

	public UserProfileTestCommand() {
		super("user", NODE);
	}

	private static void processUser(MessageChannel channel, User user) {
		String url = user.getAvatarUrl();
		channel.sendMessage(url == null ? "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png" : url).queue();
	}
}