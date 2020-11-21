package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.BooleanArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

public final class BooleanTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(BooleanArgument.get(), channel -> "boolean"), MessageCommandContext.class)
				.consumes(context -> context.getFeedbackHandler().sendFeedback(channel -> String.valueOf((boolean) context.getParsedResult(0))))
		).build();

	public BooleanTestCommand() {
		super("boolean", NODE);
	}
}
