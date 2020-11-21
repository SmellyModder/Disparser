package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.util.MessageUtil;

public final class OptionalTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(IntegerArgument.get(), channel -> "integer"), MessageCommandContext.class)
				.consumes(context -> OptionalTestCommand.process(context.getChannel(), context.getParsedResult(0)))
		).consumes(context -> {
			OptionalTestCommand.process(context.getChannel(), Integer.MAX_VALUE);
		}).build();

	public OptionalTestCommand() {
		super("optional_test", NODE);
	}

	private static void process(MessageChannel channel, int value) {
		channel.sendMessage(MessageUtil.createSuccessfulMessage("Test Integer: " + value)).queue();
	}
}