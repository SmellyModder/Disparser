package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.TestBot;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.FormattedCommandMessage;

public final class PingTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.consumes(context -> {
			TestBot.BOT.getRestPing().queue(ping -> {
				context.getFeedbackHandler().sendFeedback(new FormattedCommandMessage("Ping: `%d ms`", ping));
			});
		}).build();

	public PingTestCommand() {
		super("ping", NODE);
	}
}
