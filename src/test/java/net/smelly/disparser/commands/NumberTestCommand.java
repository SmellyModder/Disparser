package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.NumberArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

public final class NumberTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(NumberArgument.get(), channel -> "name"), MessageCommandContext.class)
				.consumes(NumberTestCommand::process)
		).build();

	public NumberTestCommand() {
		super("number", NODE);
	}

	private static void process(MessageCommandContext context) {
		Number number = context.getParsedResult(0);
		context.getFeedbackHandler().sendFeedback(channel -> number.toString());
	}
}
