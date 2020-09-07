package disparser;

import disparser.annotations.Aliases;
import disparser.annotations.Permissions;
import disparser.feedback.FeedbackHandler;
import disparser.feedback.FeedbackHandlerBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all the command execution.
 * <p> This is a {@link ListenerAdapter} so it can be used as a JDA event listener. <p>
 * 
 * @author Luke Tonon
 */
public class CommandHandler extends ListenerAdapter {
	private final Map<String, Command> aliasMap = Collections.synchronizedMap(new HashMap<>());
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
		synchronized (this.aliasMap) {
			commands.forEach(command -> {
				this.applyAliases(command, command.getClass().getAnnotation(Aliases.class));
				this.applyPermissions(command, command.getClass().getAnnotation(Permissions.class));
				for (String alias : command.getAliases()) this.aliasMap.put(alias, command);
			});
		}
	}

	/**
	 * Registers a command for an alias.
	 * @param alias - The alias for this command.
	 * @param command - The command to register.
	 */
	protected void registerCommand(String alias, Command command) {
		synchronized (this.aliasMap) {
			this.aliasMap.put(alias, command);
		}
	}

	/**
	 * Registers a command by all its aliases.
	 * @param command - The command to register.
	 */
	protected void registerCommand(Command command) {
		synchronized (this.aliasMap) {
			command.getAliases().forEach(alias -> this.aliasMap.put(alias, command));
		}
	}

	/**
	 * Applies {@link Aliases} and {@link Permissions}s to {@link Command} fields in a class.
	 * @param clazz - The class to have its fields be applied.
	 * @return This {@link CommandHandler}.
	 */
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

	/**
	 * Applies an {@link Aliases} to a {@link Command}.
	 * @param command - The command to have the {@link Aliases} applied to.
	 */
	private void applyAliases(Command command, Aliases aliases) {
		this.aliasMap.entrySet().stream().filter(entry -> entry.getValue() == command).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).forEach((alias, value) -> this.aliasMap.remove(alias));
		if (aliases != null) {
			Set<String> newAliases = aliases.mergeAliases() ? command.getAliases() : new HashSet<>();
			newAliases.addAll(Arrays.asList(aliases.value()));
			command.setAliases(newAliases);
		}
		this.registerCommand(command);
	}

	/**
	 * Applies an {@link Permissions} to a {@link Command}.
	 * @param command - The command to have the {@link Permissions} applied to.
	 */
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
			synchronized (this.aliasMap) {
				Command command = this.aliasMap.get(firstComponent.substring(prefix.length()).toLowerCase());
				if (command != null) {
					CommandContext.createContext(event, command, reader, this.getFeedbackHandlerBuilder()).ifPresent((context) -> {
						try {
							command.processCommand(context);
						} catch (Exception exception) {
							context.getFeedbackHandler().sendError(exception);
							exception.printStackTrace();
						}
					});
				}
			}
		}
	}
	
	/**
	 * Override this in your own {@link CommandHandler} if you wish to have the prefix be dynamic and/or configurable.
	 * @param guild - The guild belonging to the sent command.
	 * @return The prefix for the commands.
	 */
	public String getPrefix(Guild guild) {
		return this.prefix;
	}

	/**
	 * Gets this handler's {@link FeedbackHandlerBuilder}.
	 * This is used for creating a {@link FeedbackHandler} to be used for sending feedback when processing commands.
	 * <p> This returns {@link FeedbackHandlerBuilder#SIMPLE_BUILDER} by default. </p>
	 * @return
	 */
	public FeedbackHandlerBuilder getFeedbackHandlerBuilder() {
		return FeedbackHandlerBuilder.SIMPLE_BUILDER;
	}
}