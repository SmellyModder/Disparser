package net.smelly.disparser;

import net.smelly.disparser.commands.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.smelly.disparser.feedback.SimpleFeedbackHandler;

import javax.security.auth.login.LoginException;

public final class TestBot {
	private static JDA BOT;

	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(ActivityType.DEFAULT, "Disparsing!"));
		botBuilder.addEventListeners(
				new CommandHandler.CommandHandlerBuilder()
						.setPrefix("!")
						.registerCommands(Commands.class)
						.setFeedbackBuilder(TestCustomFeedbackHandler::new)
						.build()
		);
		BOT = botBuilder.build();
	}

	static class TestCustomFeedbackHandler extends SimpleFeedbackHandler {
		private final String pfp;

		private TestCustomFeedbackHandler(TextChannel textChannel) {
			super(textChannel);
			this.pfp = BOT.getSelfUser().getAvatarUrl();
		}

		@Override
		public void sendSuccess(String message) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setThumbnail(this.pfp);
			embedBuilder.setColor(7506394);
			embedBuilder.setTitle(":white_check_mark: " + "Command Successful");
			embedBuilder.appendDescription(message);
			this.sendFeedback(embedBuilder.build());
		}

		@Override
		public void sendError(Exception exception) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setThumbnail(this.pfp);
			embedBuilder.setColor(14495300);
			embedBuilder.setTitle(":x: " + "Command Failed");
			embedBuilder.appendDescription("**Reason: **" + exception.getMessage());
			this.sendFeedback(embedBuilder.build());
		}

	}
}