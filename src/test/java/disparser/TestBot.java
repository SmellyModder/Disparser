package disparser;

import javax.security.auth.login.LoginException;

import disparser.commands.Commands;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public final class TestBot {
	
	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(ActivityType.DEFAULT, "Disparsing!"));
		botBuilder.addEventListeners(
			new CommandHandler.CommandHandlerBuilder()
				.setPrefix("!")
				.registerCommands(Commands.class)
				.build()
		);
		botBuilder.build();
	}
	
}