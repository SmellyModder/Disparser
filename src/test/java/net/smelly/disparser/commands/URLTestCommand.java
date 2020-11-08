package net.smelly.disparser.commands;

import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.URLArgument;
import net.smelly.disparser.context.MessageCommandContext;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class URLTestCommand extends Command<MessageCommandContext> {
	private static final String USER_AGENT = "User-Agent";
	private static final String BOT_USER_AGENT = "DiscordBot (https://github.com/DV8FromTheWorld/JDA, 4.2.0_204)";

	public URLTestCommand() {
		super("url", new ConfiguredArgument<>(URLArgument.get(), channel -> "image_url", channel -> "The URL to get an image from."));
	}

	@Override
	public void processCommand(MessageCommandContext context) throws Exception {
		URL url = context.getParsedResult(0);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty(USER_AGENT, BOT_USER_AGENT);
		InputStream stream = connection.getInputStream();
		String[] split = url.getPath().split("/");
		context.getEvent().getChannel().sendFile(stream, split[split.length - 1]).queue();
	}
}
