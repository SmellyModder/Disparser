package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.jda.EmojiArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

import java.util.List;
import java.util.Random;

public final class EmojiTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(EmojiArgument.getMultipleClamped(2, 6, false), channel -> "emojis"), MessageCommandContext.class)
				.consumes(context -> {
					List<Activity.Emoji> emojis = context.getParsedResult(0);
					context.getFeedbackHandler().sendFeedback(channel -> emojis.get(new Random().nextInt(emojis.size())).getAsMention());
				})
		).build();

	public EmojiTestCommand() {
		super("emoji", NODE);
	}
}
