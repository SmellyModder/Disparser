package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;
import net.smelly.disparser.feedback.exceptions.SimpleCommandExceptionCreator;

public class EnumTestCommand extends Command {
	//Simple exception test.
	private static final SimpleCommandExceptionCreator Z_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Z is evil, it cannot be used!");

	public EnumTestCommand() {
		super("enum", EnumArgument.get(TestEnum.class));
	}

	@Override
	public void processCommand(CommandContext context) throws CommandSyntaxException {
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