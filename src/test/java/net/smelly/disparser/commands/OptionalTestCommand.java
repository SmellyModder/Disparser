package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.arguments.OptionalTestArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.util.MessageUtil;

public final class OptionalTestCommand extends Command<MessageCommandContext> {

	public OptionalTestCommand() {
		super("optional_test", new OptionalTestArgument());
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		int integer = context.getParsedResultOrElse(0, Integer.MAX_VALUE);
		context.getChannel().sendMessage(MessageUtil.createSuccessfulMessage("Test Integer: " + integer)).queue();
	}

}