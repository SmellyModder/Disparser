package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;
import net.smelly.disparser.feedback.exceptions.SimpleCommandExceptionCreator;

public final class EnumTestCommand extends Command<MessageCommandContext> {
	//Simple exception test.
	private static final SimpleCommandExceptionCreator Z_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Z is evil, it cannot be used!");

	public EnumTestCommand() {
		super("enum", ConfiguredArgument.named(EnumArgument.get(TestEnum.class), channel -> "type"));
	}

	@Override
	public void processCommand(MessageCommandContext context) throws CommandSyntaxException {
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