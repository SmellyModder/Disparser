package disparser;

import java.util.*;

import javax.annotation.Nullable;

import disparser.annotations.Optional;
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
	private Set<String> aliases;
	private Set<Permission> requiredPermissions;
	private final List<Argument<?>> arguments;
	
	public Command(String name) {
		this(name, new Argument[0]);
	}
	
	public Command(String name, Argument<?>... args) {
		this(new HashSet<>(Collections.singletonList(name)), new HashSet<>(Arrays.asList(Permission.EMPTY_PERMISSIONS)), args);
	}
	
	public Command(Set<String> aliases, Set<Permission> permissions, Argument<?>... args) {
		this.aliases = aliases;
		this.requiredPermissions = permissions;
		List<Argument<?>> setupArguments = new ArrayList<>();
		for (Argument<?> argument : args) {
			setupArguments.add(argument.getClass().isAnnotationPresent(Optional.class) ? argument.asOptional() : argument);
		}
		this.arguments = setupArguments;
	}
	
	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
	
	/**
	 * @return This command's aliases.
	 */
	public Set<String> getAliases() {
		return this.aliases;
	}
	
	public void setRequiredPermissions(Set<Permission> requiredPermissions) {
		this.requiredPermissions = requiredPermissions;
	}
	
	/**
	 * @return This command's required permissions.
	 */
	public Set<Permission> getRequiredPermissions() {
		return this.requiredPermissions;
	}
	
	/**
	 * @return This command's arguments.
	 */
	@Nullable
	public List<Argument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * Used for processing this command.
	 * 
	 * @param context - The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the {@link GuildMessageReceivedEvent} event
	 */
	public abstract void processCommand(CommandContext context);
	
	public boolean hasPermissions(Member member) {
		return member.hasPermission(this.getRequiredPermissions());
	}
	
	public boolean testForPermissions(Message message, Permission... permission) {
		Member member = message.getMember();
		if (member != null && member.hasPermission(permission)) {
			return true;
		}
		this.sendMessage(message.getTextChannel(), MessageUtil.createErrorMessage("You do not have permission to run this command"));
		return false;
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