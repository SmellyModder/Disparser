package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.primitive.NumberArgument;

public final class NumberTestCommand extends Command {

    public NumberTestCommand() {
        super("number", NumberArgument.get());
    }

    @Override
    public void processCommand(CommandContext context) {
        Number number = context.getParsedResult(0);
        this.sendMessage(context.getEvent().getChannel(), number.toString());
    }

}
