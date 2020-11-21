package net.smelly.disparser.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.StringArgument;
import net.smelly.disparser.arguments.jda.TextChannelArgument;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

public final class RenameChannelTestCommand extends Command<GuildMessageReceivedEvent, GuildMessageCommandContext> {
	private static final RootNode<GuildMessageReceivedEvent, GuildMessageCommandContext> NODE = RootNode.Builder.create(GuildMessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(TextChannelArgument.get(), channel -> "channel"), GuildMessageCommandContext.class)
				.then(
					ArgumentNode.Builder.create(ConfiguredArgument.named(StringArgument.get(), channel -> "name"), GuildMessageCommandContext.class)
						.consumes(context -> {
							((TextChannel) context.getParsedResult(0)).getManager().setName(context.getParsedResult(1)).queue();
						})
				)
		).then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(StringArgument.get(), channel -> "name"), GuildMessageCommandContext.class)
				.consumes(context -> {
					context.getChannel().getManager().setName(context.getParsedResult(1)).queue();
				})
		).build();

	public RenameChannelTestCommand() {
		super("rename", NODE);
	}
}