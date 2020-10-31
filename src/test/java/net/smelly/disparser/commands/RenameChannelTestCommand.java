package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.smelly.disparser.Command;
import net.smelly.disparser.arguments.java.StringArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.context.GuildMessageCommandContext;

public final class RenameChannelTestCommand extends Command<GuildMessageCommandContext> {

	public RenameChannelTestCommand() {
		super("rename", TextChannelArgument.get().asOptional(), StringArgument.get());
	}

	@Override
	public void processCommand(GuildMessageCommandContext context) {
		TextChannel channel = context.getParsedResultOrElse(0, context.getChannel());
		channel.getManager().setName(context.getParsedResult(1)).queue();
	}

}