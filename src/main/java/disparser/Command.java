package disparser;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Abstract class for a command.
 * 
 * @author Luke Tonon
 */
public abstract class Command {
	private final List<Argument<?>> arguments;
	private final String[] aliases;
	
	public Command(String name) {
		this(name, new Argument[0]);
	}
	
	public Command(String name, Argument<?>... args) {
		this(new String[] {name}, args);
	}
	
	public Command(String[] aliases, Argument<?>... args) {
		this.aliases = aliases;
		this.arguments = Arrays.asList(args);
	}
	
	/**
	 * @return This command's arguments.
	 */
	@Nullable
	public List<Argument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return This command's aliases.
	 */
	public String[] getAliases() {
		return this.aliases;
	}
	
	/**
	 * Used for processing this command.
	 * 
	 * @param context - The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the {@link GuildMessageReceivedEvent} event
	 */
	public abstract void processCommand(CommandContext context);
	
	protected boolean testForAdmin(Message message) {
		if (this.isMessageFromAdmin(message)) {
			return true;
		}
		this.sendMessage(message.getTextChannel(), MessageUtil.createErrorMessage("You do not have permission to run this command"));
		return false;
	}
	
	protected boolean isMessageFromAdmin(Message message) {
		Member member = message.getMember();
		return message.getMember().isOwner() || member.hasPermission(Permission.ADMINISTRATOR);
	}
	
	protected void sendMessage(TextChannel channel, CharSequence message) {
		channel.sendTyping().queue();
		channel.sendMessage(message).queue();
	}
	
	protected void sendMessage(TextChannel channel, MessageEmbed message) {
		channel.sendTyping().queue();
		channel.sendMessage(message).queue();
	}
}