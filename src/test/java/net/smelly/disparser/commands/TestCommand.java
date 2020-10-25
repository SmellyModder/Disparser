package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;

public class TestCommand extends Command {

	public TestCommand() {
		super("test");
	}

	@Override
	public void processCommand(CommandContext context) {
		context.getFeedbackHandler().sendFeedback(channel -> "This is a Test!");
	}

}