package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.jda.EmojiArgument;

import java.util.List;
import java.util.Random;

public final class EmojiTestCommand extends Command {

	public EmojiTestCommand() {
		super("emoji", EmojiArgument.getMultipleClamped(2, 6, false));
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		List<Activity.Emoji> emojis = context.getParsedResult(0);
		context.getFeedbackHandler().sendFeedback(emojis.get(new Random().nextInt(emojis.size())).getAsMention());
	}

}
