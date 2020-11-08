package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.User;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.EitherArgument;
import net.smelly.disparser.arguments.EitherArgument.Either;
import net.smelly.disparser.arguments.java.CharArgument;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.arguments.jda.UserArgument;
import net.smelly.disparser.context.MessageCommandContext;

/**
 * Here is an example of a command that has many possibilities.
 */
public final class ComplexTestCommand extends Command<MessageCommandContext> {

	public ComplexTestCommand() {
		super(
				"complex",
				ConfiguredArgument.namedOptional(TextChannelArgument.get(), channel -> "other_channel"),
				ConfiguredArgument.named(EitherArgument.of(IntegerArgument.get(), UserArgument.get()), channel -> "integer|user"),
				ConfiguredArgument.named(FloatArgument.getClamped(0.0F, 100.0F), channel -> "score"),
				ConfiguredArgument.namedOptional(CharArgument.get(), channel -> "character")
		);
	}

	@Override
	public void processCommand(MessageCommandContext context) {
		Either<Integer, User> either = context.getParsedResult(1);
		float afloat = context.getParsedResult(2);
		char character = context.getParsedResultOrElse(3, 'X');
		context.getParsedResultOrElse(0, context.getChannel()).sendMessage(either.convertTo((eitherIn) -> eitherIn.first != null ? "#" + either.first : either.second.getAsMention()) + String.format(" got a %f accuracy score for the character %c", afloat, character)).queue();
	}

}