package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.jda.EmojiArgument;
import net.smelly.disparser.context.MessageCommandContext;

import java.util.List;
import java.util.Random;

public final class EmojiTestCommand extends Command<MessageCommandContext> {

	public EmojiTestCommand() {
		super("emoji", ConfiguredArgument.named(EmojiArgument.getMultipleClamped(2, 6, false), channel -> "emojis"));
	}

	@Override
	public void processCommand(MessageCommandContext context) throws Exception {
		List<Activity.Emoji> emojis = context.getParsedResult(0);
		context.getFeedbackHandler().sendFeedback(channel -> emojis.get(new Random().nextInt(emojis.size())).getAsMention());
	}

}
