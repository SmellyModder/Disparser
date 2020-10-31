package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.User;
import net.smelly.disparser.Command;
import net.smelly.disparser.arguments.EitherArgument;
import net.smelly.disparser.arguments.EitherArgument.Either;
import net.smelly.disparser.arguments.java.CharArgument;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.MessageCommandContext;

public final class ComplexTestCommand extends Command<MessageCommandContext> {

	public ComplexTestCommand() {
		super("complex", TextChannelArgument.get().asOptional(), EitherArgument.of(IntegerArgument.get(), UserArgument.get()), FloatArgument.getClamped(0.0F, 100.0F), CharArgument.get().asOptional());
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		Either<Integer, User> either = context.getParsedResult(1);
		float afloat = context.getParsedResult(2);
		char character = context.getParsedResultOrElse(3, 'X');
		context.getChannel().sendMessage(either.convertTo((eitherIn) -> eitherIn.first != null ? "#" + either.first : either.second.getAsMention()) + String.format(" got a %f accuracy score for the character %c", afloat, character)).queue();
	}

}