package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;
import net.smelly.disparser.util.MessageUtil;

public final class TestFeedbackHandler implements FeedbackHandler {
	private final TextChannel channel;

	public TestFeedbackHandler(TextChannel channel) {
		this.channel = channel;
	}

	@Override
	public void sendFeedback(CommandMessage commandMessage) {
		this.channel.sendMessage(commandMessage.getMessage(this.channel)).queue();
	}

	@Override
	public void sendFeedback(MessageEmbed messageEmbed) {
		this.channel.sendMessage(messageEmbed).queue();
	}

	@Override
	public void sendSuccess(CommandMessage message) {
		this.channel.sendMessage(MessageUtil.createSuccessfulMessage(message.getMessage(this.channel))).queue();
	}

	@Override
	public void sendError(Exception exception) {
		if (exception instanceof CommandSyntaxException) {
			this.channel.sendMessage(MessageUtil.createErrorMessage(((CommandSyntaxException) exception).getCommandMessage().getMessage(this.channel))).queue();
		} else {
			this.channel.sendMessage(MessageUtil.createErrorMessage(exception.getMessage())).queue();
		}
	}
}
