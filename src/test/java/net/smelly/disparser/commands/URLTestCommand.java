package net.smelly.disparser.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.URLArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class URLTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(new ConfiguredArgument<>(URLArgument.get(), channel -> "image_url", channel -> "The URL to get an image from."), MessageCommandContext.class)
				.consumes(URLTestCommand::process)
		).build();
	private static final String USER_AGENT = "User-Agent";
	private static final String BOT_USER_AGENT = "DiscordBot (https://github.com/DV8FromTheWorld/JDA, 4.2.0_204)";

	public URLTestCommand() {
		super("url", NODE);
	}

	private static void process(MessageCommandContext context) {
		URL url = context.getParsedResult(0);
		try {
			URLConnection connection = url.openConnection();
			connection.setRequestProperty(USER_AGENT, BOT_USER_AGENT);
			InputStream stream = connection.getInputStream();
			String[] split = url.getPath().split("/");
			context.getEvent().getChannel().sendFile(stream, split[split.length - 1]).queue();
		} catch (IOException exception) {
			context.getFeedbackHandler().sendFeedback(channel -> "Test failed");
		}
	}
}
