package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.jda.UserArgument;
import net.dv8tion.jda.api.entities.User;

public class UserProfileTestCommand extends Command {

	public UserProfileTestCommand() {
		super("user", UserArgument.get());
	}

	@Override
	public void processCommand(CommandContext context) {
		User user = context.getParsedResult(0);
		String url = user.getAvatarUrl();
		context.getEvent().getChannel().sendMessage(url == null ? "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png" : url).queue();
	}

}