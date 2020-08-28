package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.util.MessageUtil;
import disparser.arguments.OptionalTestArgument;

public class OptionalTestCommand extends Command {

	public OptionalTestCommand() {
		super("optional_test", new OptionalTestArgument());
	}

	@Override
	public void processCommand(CommandContext context) {
		int integer = context.getParsedResultOrElse(0, Integer.MAX_VALUE);
		context.getEvent().getChannel().sendMessage(MessageUtil.createSuccessfulMessage("Test Integer: " + integer)).queue();
	}

}