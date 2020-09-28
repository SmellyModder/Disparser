package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.jda.EmojiArgument;

public final class EmojiTestCommand extends Command {

	public EmojiTestCommand() {
		super("emoji", EmojiArgument.get());
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		Activity.Emoji emoji = context.getParsedResult(0);
		context.getFeedbackHandler().sendFeedback(emoji.getAsMention());
	}

}
