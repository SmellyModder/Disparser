package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.EnumArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;
import net.smelly.disparser.feedback.exceptions.SimpleCommandExceptionCreator;

public class EnumTestCommand extends Command {
	//Simple exception test.
	private static final SimpleCommandExceptionCreator Z_EXCEPTION = new SimpleCommandExceptionCreator("Z is evil, it cannot be used!");

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
		A("Alphabet Soup"),
		B("Bees..."),
		C("Chad"),
		Z("");

		private final String message;

		TestEnum(String message) {
			this.message = message;
		}
	}
}