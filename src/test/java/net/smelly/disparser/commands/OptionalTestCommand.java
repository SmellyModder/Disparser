package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.OptionalTestArgument;
import net.smelly.disparser.util.MessageUtil;

public class OptionalTestCommand extends Command {

	public OptionalTestCommand() {
		super("optional_test", new OptionalTestArgument());
	}

	@Override
	public void processCommand(CommandContext context) {
		int integer = context.getParsedResultOrElse(0, Integer.MAX_VALUE);
		context.getEvent().getChannel().sendMessage(MessageUtil.createSuccessfulMessage("Test Integer: " + integer)).queue();
	}

}