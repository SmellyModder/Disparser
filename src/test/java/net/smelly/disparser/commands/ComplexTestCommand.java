package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.CommandContext;
import net.smelly.disparser.arguments.EitherArgument;
import net.smelly.disparser.arguments.EitherArgument.Either;
import net.smelly.disparser.arguments.java.CharArgument;
import net.smelly.disparser.arguments.java.FloatArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.arguments.jda.UserArgument;

public class ComplexTestCommand extends Command {

	public ComplexTestCommand() {
		super("complex", TextChannelArgument.get().asOptional(), EitherArgument.of(IntegerArgument.get(), UserArgument.get()), FloatArgument.getClamped(0.0F, 100.0F), CharArgument.get().asOptional());
	}

	@Override
	public void processCommand(CommandContext context) {
		GuildMessageReceivedEvent event = context.getEvent();
		TextChannel channel = context.getParsedResultOrElse(0, event.getChannel());
		Either<Integer, User> either = context.getParsedResult(1);
		float afloat = context.getParsedResult(2);
		char character = context.getParsedResultOrElse(3, 'X');
		channel.sendMessage(either.convertTo((eitherIn) -> eitherIn.first != null ? "#" + either.first : either.second.getAsMention()) + String.format(" got a %f accuracy score for the character %c", afloat, character)).queue();
	}

}