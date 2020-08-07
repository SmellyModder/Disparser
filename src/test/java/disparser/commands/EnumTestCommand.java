package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.EnumTestArgument;
import disparser.arguments.EnumTestArgument.ArgumentEnum;

public class EnumTestCommand extends Command {

	public EnumTestCommand() {
		super("enum", new EnumTestArgument());
	}

	@Override
	public void processCommand(CommandContext context) {
		ArgumentEnum argumentEnum = context.getParsedResult(0);
		context.getEvent().getChannel().sendMessage(argumentEnum.message).queue();
	}

}