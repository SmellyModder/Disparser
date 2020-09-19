package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.java.EnumArgument;
import disparser.feedback.CommandSyntaxException;
import disparser.feedback.SimpleCommandExceptionCreator;

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