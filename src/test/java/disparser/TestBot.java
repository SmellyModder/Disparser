package disparser;

import javax.security.auth.login.LoginException;

import disparser.annotations.Aliases;
import disparser.annotations.Permissions;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public final class TestBot {
	@Permissions({Permission.ADMINISTRATOR})
	@Aliases(value = {"optional", "test_optional"}, mergeAliases = true)
	private static final OptionalTestCommand OPTIONAL_TEST = new OptionalTestCommand();
	
	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.DEFAULT));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(ActivityType.DEFAULT, "Testing"));
		botBuilder.addEventListeners(
			new CommandHandler("!",
				OPTIONAL_TEST
			).applyAnnotations(TestBot.class)
		);
		botBuilder.build();
	}
}