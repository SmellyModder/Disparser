package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.RootNode;

public final class TestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE =
			RootNode.Builder.create(MessageCommandContext.class)
					.consumes(context -> {
						context.getFeedbackHandler().sendFeedback(channel -> "This is a test!");
					})
			.build();

	public TestCommand() {
		super("test", NODE);
	}
}