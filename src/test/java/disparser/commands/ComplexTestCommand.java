package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.EitherArgument;
import disparser.arguments.EitherArgument.Either;
import disparser.arguments.jda.TextChannelArgument;
import disparser.arguments.jda.UserArgument;
import disparser.arguments.primitive.CharArgument;
import disparser.arguments.primitive.FloatArgument;
import disparser.arguments.primitive.IntegerArgument;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ComplexTestCommand extends Command {

	public ComplexTestCommand() {
		super("complex", TextChannelArgument.get().asOptional(), EitherArgument.of(IntegerArgument.get(), UserArgument.get()), FloatArgument.get(), CharArgument.get().asOptional());
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