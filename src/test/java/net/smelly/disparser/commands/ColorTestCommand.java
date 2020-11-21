package net.smelly.disparser.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.ColorArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

public final class ColorTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(ColorArgument.get(), channel -> "color"), MessageCommandContext.class)
				.consumes(context -> {
					context.getFeedbackHandler().sendFeedback(new EmbedBuilder().addBlankField(false).setColor(context.getParsedResult(0)).build());
				})
		).build();

	public ColorTestCommand() {
		super("color", NODE);
	}
}
