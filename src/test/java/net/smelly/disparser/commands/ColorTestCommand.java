package net.smelly.disparser.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.ColorArgument;
import net.smelly.disparser.context.MessageCommandContext;

public final class ColorTestCommand extends Command<MessageCommandContext> {

	public ColorTestCommand() {
		super("color", ConfiguredArgument.named(ColorArgument.get(), channel -> "color"));
	}

	@Override
	public void processCommand(MessageCommandContext context) throws Exception {
		context.getFeedbackHandler().sendFeedback(
				new EmbedBuilder().addBlankField(false).setColor(context.getParsedResult(0)).build()
		);
	}

}
