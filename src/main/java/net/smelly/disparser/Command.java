package net.smelly.disparser;

import net.smelly.disparser.annotations.Optional;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.*;

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

	/**
	 * Used for processing this command.
	 *
	 * @param context - The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the {@link GuildMessageReceivedEvent} event
	 */
	public abstract void processCommand(CommandContext context) throws Exception;

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

	public boolean hasPermissions(Member member) {
		return member.hasPermission(this.getRequiredPermissions());
	}
}