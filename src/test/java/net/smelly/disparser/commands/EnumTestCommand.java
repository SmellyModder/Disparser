package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.feedback.exceptions.SimpleCommandExceptionCreator;

public final class EnumTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(EnumArgument.get(TestEnum.class), channel -> "type"), MessageCommandContext.class)
				.consumes(EnumTestCommand::process)
		).build();
	//Simple exception test.
	private static final SimpleCommandExceptionCreator Z_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Z is evil, it cannot be used!");

	public EnumTestCommand() {
		super("enum", NODE);
	}

	private static void process(MessageCommandContext context) throws CommandException {
		TestEnum testEnum = context.getParsedResult(0);
		if (testEnum == TestEnum.Z) {
			throw Z_EXCEPTION.create();
		} else {
			context.getFeedbackHandler().sendSuccess(testEnum.message);
		}
	}

	enum TestEnum {
		A(channel -> "Alphabet Soup"),
		B(channel -> "Bees..."),
		C(channel -> "Chad"),
		Z(channel -> "");

		private final CommandMessage message;

		TestEnum(CommandMessage message) {
			this.message = message;
		}
	}
}