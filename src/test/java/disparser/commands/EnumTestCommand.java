package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.primitive.EnumArgument;

public class EnumTestCommand extends Command {

	public EnumTestCommand() {
		super("enum", EnumArgument.get(TestEnum.class));
	}

	@Override
	public void processCommand(CommandContext context) {
		TestEnum testEnum = context.getParsedResult(0);
		context.getEvent().getChannel().sendMessage(testEnum.message).queue();
	}

	enum TestEnum {
		A("Alphabet Soup"),
		B("Bees..."),
		C("Chad");
		
		private final String message;
		
		TestEnum(String message) {
			this.message = message;
		}
	}
	
}