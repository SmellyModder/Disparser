package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.NumberArgument;
import net.smelly.disparser.context.MessageCommandContext;

public final class NumberTestCommand extends Command<MessageCommandContext> {

	public NumberTestCommand() {
		super("number", ConfiguredArgument.named(NumberArgument.get(), channel -> "name"));
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		Number number = context.getParsedResult(0);
		context.getFeedbackHandler().sendFeedback(channel -> number.toString());
	}

}
