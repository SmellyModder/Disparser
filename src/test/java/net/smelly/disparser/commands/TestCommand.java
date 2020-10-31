package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.context.MessageCommandContext;

public final class TestCommand extends Command<MessageCommandContext> {

	public TestCommand() {
		super("test");
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		context.getFeedbackHandler().sendFeedback(channel -> "This is a Test!");
	}

}