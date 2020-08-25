package disparser;

import disparser.arguments.primitive.StringArgument;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple base command for an info command.
 * 
 * @author Luke Tonon
 */
public class InfoCommand extends Command {
	private MessageEmbed mainInfoMessage;
	private Map<String, MessageEmbed> commandInfoMessages;
	
	public InfoCommand(MessageEmbed mainInfoMessage) {
		super("info", StringArgument.get().asOptional());
		this.mainInfoMessage = mainInfoMessage;
	}
	
	public InfoCommand setMainInfoMessages(MessageEmbed mainInfoMessage) {
		this.mainInfoMessage = mainInfoMessage;
		return this;
	}
	
	public InfoCommand setCommandInfoMessages(Map<String, MessageEmbed> commandInfoMessages) {
		this.commandInfoMessages = commandInfoMessages;
		return this;
	}
	
	public InfoCommand putCommandInfo(String commandName, MessageEmbed commandInfo) {
		if (this.commandInfoMessages == null) this.commandInfoMessages = Collections.synchronizedMap(new HashMap<>());
		this.commandInfoMessages.put(commandName, commandInfo);
		return this;
	}

	@Override
	public void processCommand(CommandContext context) {
		TextChannel channel = context.getEvent().getChannel();
		if (this.commandInfoMessages != null) {
			String commandName = context.getParsedResult(0);
			if (commandName != null) {
				MessageEmbed commandDescription = this.commandInfoMessages.get(commandName);
				if (commandDescription != null) {
					channel.sendMessage(commandDescription).queue();
					return;
				}
			}
		}
		channel.sendMessage(this.mainInfoMessage).queue();
	}
}