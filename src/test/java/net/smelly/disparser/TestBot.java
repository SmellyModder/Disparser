package net.smelly.disparser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.smelly.disparser.commands.Commands;
import net.smelly.disparser.concurrent.DisparsingThreadFactory;
import net.smelly.disparser.context.handlers.CommandHandler;
import net.smelly.disparser.context.handlers.GuildCommandHandler;
import net.smelly.disparser.feedback.TestExceptionProvider;
import net.smelly.disparser.feedback.TestFeedbackHandler;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;

public final class TestBot {
	private static JDA BOT;

	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(ActivityType.DEFAULT, "Disparsing!"));
		botBuilder.addEventListeners(
				new CommandHandler.Builder()
						.setPrefix("c!")
						.registerCommands(Commands.class)
						.setFeedbackBuilder(TestFeedbackHandler::new)
						.setExceptionProviderBuilder(channel -> TestExceptionProvider.INSTANCE)
						.setExecutorService(Executors.newFixedThreadPool(6, new DisparsingThreadFactory("Test")))
						.build(),
				new GuildCommandHandler.Builder()
						.setPrefix("g!")
						.registerCommands(Commands.ROLE_TEST_COMMAND, Commands.RENAME_CHANNEL_TEST)
						.build()
		);
		BOT = botBuilder.build();
	}
}