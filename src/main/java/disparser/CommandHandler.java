package disparser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import disparser.annotations.Aliases;
import disparser.annotations.Permissions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Handles all the command execution.
 * <p> This is a {@link ListenerAdapter}. <p>
 * 
 * @author Luke Tonon
 */
public class CommandHandler extends ListenerAdapter {
	private final Map<String, Command> COMMANDS = Collections.synchronizedMap(new HashMap<String, Command>());
	private String prefix = "!";
	
	public CommandHandler() {}
	
	public CommandHandler(String prefix) {
		this.prefix = prefix;
	}
	
	public CommandHandler(String prefix, List<Command> commands) {
		this(prefix);
		this.registerCommands(commands);
	}
	
	public CommandHandler(String prefix, Command... commands) {
		this(prefix, Arrays.asList(commands));
	}
	
	protected void registerCommands(List<Command> commands) {
		synchronized (COMMANDS) {
			commands.forEach(command -> {
				this.applyAliases(command, command.getClass().getAnnotation(Aliases.class));
				this.applyPermissions(command, command.getClass().getAnnotation(Permissions.class));
				for (String alias : command.getAliases()) COMMANDS.put(alias, command);
			});
		}
	}
	
	protected void registerCommand(String commandName, Command command) {
		synchronized (COMMANDS) {
			COMMANDS.put(commandName, command);
		}
	}
	
	protected void registerCommand(Command command) {
		synchronized (COMMANDS) {
			command.getAliases().forEach(alias -> COMMANDS.put(alias, command));
		}
	}
	
	public CommandHandler applyAnnotations(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Aliases aliases = field.getAnnotation(Aliases.class);
			Permissions permissions = field.getAnnotation(Permissions.class);
			field.setAccessible(true);
			try {
				Object object = field.get(clazz.newInstance());
				if (!(object instanceof Command)) return this;
				Command command = (Command) object;
				if (aliases != null) {
					this.applyAliases(command, aliases);
				}
				if (permissions != null) {
					this.applyPermissions(command, permissions);
				}
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	private void applyAliases(Command command, Aliases aliases) {
		if (aliases != null) {
			Set<String> newAliases = aliases.mergeAliases() ? command.getAliases() : new HashSet<>();
			newAliases.addAll(Arrays.asList(aliases.value()));
			command.setAliases(newAliases);
		}
		this.registerCommand(command);
	}
	
	private void applyPermissions(Command command, Permissions permissions) {
		if (permissions != null) {
			Set<Permission> newPermissions = permissions.mergePermissions() ? command.getRequiredPermissions() : new HashSet<>();
			newPermissions.addAll(Arrays.asList(permissions.value()));
			command.setRequiredPermissions(newPermissions);
		}
		this.registerCommand(command);
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		ArgumentReader reader = ArgumentReader.create(event.getMessage());
		String firstComponent = reader.getCurrentMessageComponent();
		String prefix = this.getPrefix(event.getGuild());
		if (firstComponent.startsWith(prefix)) {
			synchronized (COMMANDS) {
				Command command = COMMANDS.get(firstComponent.substring(prefix.length()).toLowerCase());
				if (command != null) CommandContext.createContext(event, command, reader).ifPresent(command::processCommand);
			}
		}
	}
	
	/**
	 * Override this in your own {@link CommandHandler} if you wish to have the prefix be dynamic.
	 * @param guild - The guild belonging to the sent command.
	 * @return The prefix for the commands.
	 */
	public String getPrefix(Guild guild) {
		return this.prefix;
	}
}