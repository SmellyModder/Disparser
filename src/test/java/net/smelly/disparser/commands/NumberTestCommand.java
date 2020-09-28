package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.java.NumberArgument;

public final class NumberTestCommand extends Command {

    public NumberTestCommand() {
        super("number", NumberArgument.get());
    }

    @Override
    public void processCommand(CommandContext context) {
        Number number = context.getParsedResult(0);
        context.getFeedbackHandler().sendFeedback(number.toString());
    }

}
