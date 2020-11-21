package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.CharArgument;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

/**
 * Here is an example of a command that has many possibilities.
 */
public final class ComplexTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	/**
	 * Complex might even be an understatement...
	 */
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(TextChannelArgument.get(), channel -> "other_channel"), MessageCommandContext.class)
				.then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(UserArgument.get(), channel -> "user"), MessageCommandContext.class)
						.then(
							ArgumentNode.Builder.create(ConfiguredArgument.named(FloatArgument.getClamped(0.0F, 100.0F), channel -> "score"), MessageCommandContext.class)
								.then(
									ArgumentNode.Builder.create(ConfiguredArgument.named(CharArgument.get(), channel -> "character"), MessageCommandContext.class)
										.consumes(context -> {
											process(context.getParsedResult(0), ((User) context.getParsedResult(1)).getAsMention(), context.getParsedResult(2), context.getParsedResult(3));
										})
								).consumes(context -> {
									process(context.getParsedResult(0), ((User) context.getParsedResult(1)).getAsMention(), context.getParsedResult(2), 'X');
								})
						).then(
							ArgumentNode.Builder.create(ConfiguredArgument.named(IntegerArgument.get(), channel -> "integer"), MessageCommandContext.class)
								.then(
									ArgumentNode.Builder.create(ConfiguredArgument.named(FloatArgument.getClamped(0.0F, 100.0F), channel -> "score"), MessageCommandContext.class)
										.then(
											ArgumentNode.Builder.create(ConfiguredArgument.named(CharArgument.get(), channel -> "character"), MessageCommandContext.class)
												.consumes(context -> {
													process(context.getParsedResult(0), ((User) context.getParsedResult(1)).getAsMention(), context.getParsedResult(2), context.getParsedResult(3));
												})
										).consumes(context -> {
											process(context.getParsedResult(0), String.valueOf(context.getParsedResult(1)), context.getParsedResult(2), 'X');
										})
								)
						)
				)
		).then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(UserArgument.get(), channel -> "user"), MessageCommandContext.class)
				.then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(FloatArgument.getClamped(0.0F, 100.0F), channel -> "score"), MessageCommandContext.class)
						.then(
							ArgumentNode.Builder.create(ConfiguredArgument.named(CharArgument.get(), channel -> "character"), MessageCommandContext.class)
								.consumes(context -> {
									process(context.getChannel(), ((User) context.getParsedResult(0)).getAsMention(), context.getParsedResult(1), context.getParsedResult(2));
								})
						).consumes(context -> {
								process(context.getChannel(), ((User) context.getParsedResult(0)).getAsMention(), context.getParsedResult(1), 'X');
						})
				).then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(IntegerArgument.get(), channel -> "integer"), MessageCommandContext.class)
						.then(
							ArgumentNode.Builder.create(ConfiguredArgument.named(FloatArgument.getClamped(0.0F, 100.0F), channel -> "score"), MessageCommandContext.class)
								.then(
									ArgumentNode.Builder.create(ConfiguredArgument.named(CharArgument.get(), channel -> "character"), MessageCommandContext.class)
										.consumes(context -> {
											process(context.getChannel(), ((User) context.getParsedResult(0)).getAsMention(), context.getParsedResult(1), context.getParsedResult(2));
										})
								).consumes(context -> {
									process(context.getChannel(), String.valueOf(context.getParsedResult(0)), context.getParsedResult(1), 'X');
								})
						)
				)
		).build();

	public ComplexTestCommand() {
		super("complex", NODE);
	}

	private static void process(MessageChannel channel, String mentionOrNumber, float score, char character) {
		channel.sendMessage(mentionOrNumber + String.format(" got a %f accuracy score for the character %c", score, character)).queue();
	}
}