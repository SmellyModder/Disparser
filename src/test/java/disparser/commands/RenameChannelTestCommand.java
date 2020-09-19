package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.jda.TextChannelArgument;
import disparser.arguments.java.StringArgument;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RenameChannelTestCommand extends Command {

	public RenameChannelTestCommand() {
		super("rename", TextChannelArgument.get().asOptional(), StringArgument.get());
	}

	@Override
	public void processCommand(CommandContext context) {
		GuildMessageReceivedEvent event = context.getEvent();
		TextChannel channel = context.getParsedResultOrElse(0, event.getChannel());
		channel.getManager().setName(context.getParsedResult(1)).queue();
	}

}