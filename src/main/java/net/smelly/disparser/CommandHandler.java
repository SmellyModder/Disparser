package net.smelly.disparser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Permissions;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.properties.AliasesProperty;
import net.smelly.disparser.properties.CommandProperty;
import net.smelly.disparser.properties.CommandPropertyMap;
import net.smelly.disparser.properties.PermissionsProperty;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * A class for processing execution of commands.
 * <p>If you wish to make complex use of Disparser's more customizable features for command processing extend this class or design your own command handler.</p>
 * <p> This is a {@link ListenerAdapter} so it can be used as a JDA event listener. <p>
 *
 * @author Luke Tonon
 */
public class CommandHandler extends ListenerAdapter {
	protected final ConcurrentHashMap<String, Command> aliasMap = new ConcurrentHashMap<>();
	protected final CommandPropertyMap commandPropertyMap;
	protected final Function<Guild, String> prefixFunction;
	protected final FeedbackHandlerBuilder feedbackHandlerBuilder;
	protected final ExecutorService executorService;

	protected CommandHandler(CommandPropertyMap commandPropertyMap, Function<Guild, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, ExecutorService executorService) {
		this.commandPropertyMap = commandPropertyMap;
		this.prefixFunction = prefixFunction;
		this.feedbackHandlerBuilder = feedbackHandlerBuilder;
		this.executorService = executorService;
	}

	protected CommandHandler(String prefix, FeedbackHandlerBuilder feedbackHandlerBuilder) {
		this(CommandPropertyMap.createEmpty(), guild -> prefix, feedbackHandlerBuilder, Executors.newSingleThreadExecutor());
	}

	protected void registerCommands(Collection<Command> command) {
		command.forEach(this::registerCommand);
	}

	protected void registerCommand(Command command) {
		this.commandPropertyMap.putCommand(command);
	}

	@SuppressWarnings("unchecked")
	protected void registerCommand(Command command, Map<CommandProperty<?, ?>, CommandProperty.Value<?>> map) {
		this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
		for (String alias : (Set<String>) map.computeIfAbsent(command.getAliasesProperty(), (key) -> CommandProperty.Value.create(command.getAliasesProperty())).get()) {
			this.aliasMap.put(alias, command);
		}
		CommandPropertyMap.PropertyMap propertyMap = this.commandPropertyMap.getAndClearPropertyMap(command);
		propertyMap.putAll(map);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		this.executorService.execute(() -> {
			CommandContext.createAndDisparse(this, event);
		});
	}

	/**
	 * Gets this handler's {@link CommandPropertyMap}.
	 *
	 * @return This handler's {@link CommandPropertyMap}.
	 */
	public CommandPropertyMap getCommandPropertyMap() {
		return this.commandPropertyMap;
	}

	/**
	 * Gets the permissions for a given {@link Command}.
	 *
	 * @param command The {@link Command} to get the permissions for.
	 * @return The permissions for the given {@link Command}.
	 */
	public UnmodifiableSet<Permission> getPermissions(Command command) {
		return this.commandPropertyMap.getPropertyMap(command).get(command.getPermissionsProperty()).get();
	}

	/**
	 * Override this in your own {@link CommandHandler} if you wish to have the prefix be more dynamic and/or configurable.
	 *
	 * @param guild - The guild belonging to the sent command.
	 * @return The prefix for the commands.
	 */
	public String getPrefix(Guild guild) {
		return this.prefixFunction.apply(guild);
	}

	/**
	 * Gets this handler's {@link FeedbackHandlerBuilder}.
	 * This is used for creating a {@link FeedbackHandler} to be used for sending feedback when processing commands.
	 * <p> This returns {@link FeedbackHandlerBuilder#SIMPLE_BUILDER} by default. </p>
	 *
	 * @return This {@link FeedbackHandlerBuilder} for this {@link CommandHandler}.
	 */
	public FeedbackHandlerBuilder getFeedbackHandlerBuilder() {
		return this.feedbackHandlerBuilder;
	}

	public static class CommandHandlerBuilder {
		private final Map<String, Command> aliasMap = new HashMap<>();
		private final Map<Command, CommandPropertyMap.PropertyMap> commandPropertyMap = new HashMap<>();
		private Function<Guild, String> prefixFunction = guild -> "!";
		private FeedbackHandlerBuilder feedbackHandlerBuilder = FeedbackHandlerBuilder.SIMPLE_BUILDER;
		private ExecutorService executorService = Executors.newSingleThreadExecutor();

		/**
		 * Sets a prefix for the {@link CommandHandler}.
		 *
		 * @param prefix - The prefix to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setPrefix(String prefix) {
			this.prefixFunction = (guild) -> prefix;
			return this;
		}

		/**
		 * Sets a prefix function for the {@link CommandHandler}.
		 *
		 * @param prefixFunction - The prefix function to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setPrefix(Function<Guild, String> prefixFunction) {
			this.prefixFunction = prefixFunction;
			return this;
		}

		/**
		 * Registers a command for an alias.
		 *
		 * @param alias   - The alias for this command.
		 * @param command - The command to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommand(String alias, Command command) {
			this.aliasMap.put(alias, command);
			return this;
		}

		/**
		 * Registers a command by all its aliases.
		 *
		 * @param command - The command to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommand(Command command) {
			this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
			AliasesProperty aliasesProperty = command.getAliasesProperty();
			UnmodifiableSet<String> aliases = aliasesProperty.get(null);
			for (String alias : aliases) {
				this.aliasMap.put(alias, command);
			}
			this.commandPropertyMap.computeIfAbsent(command, (key) -> new CommandPropertyMap.PropertyMap()).putOrModify(aliasesProperty, aliases);
			return this;
		}

		/**
		 * Registers a command with {@link Aliases} and {@link Permissions} annotations.
		 * <p>This method is simply a way to further customize the aliases and permissions of a command when you want to use an immutable command.</p>
		 *
		 * @param command     - The command to register.
		 * @param aliases     - The {@link Aliases} annotation to use with this command.
		 * @param permissions - The {@link Permissions} annotation to use with this command.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommand(Command command, @Nullable Aliases aliases, @Nullable Permissions permissions) {
			AliasesProperty aliasesProperty = command.getAliasesProperty();
			UnmodifiableSet<String> commandAliases = aliasesProperty.get(aliases);
			this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
			for (String alias : commandAliases) {
				this.aliasMap.put(alias, command);
			}
			PermissionsProperty permissionsProperty = command.getPermissionsProperty();
			this.commandPropertyMap.computeIfAbsent(command, (key) -> new CommandPropertyMap.PropertyMap()).putOrModify(aliasesProperty, commandAliases).putOrModify(permissionsProperty, permissionsProperty.get(permissions));
			return this;
		}

		/**
		 * Registers multiple commands by their aliases.
		 *
		 * @param commands - The commands to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommands(Command... commands) {
			for (Command command : commands) {
				this.registerCommand(command);
			}
			return this;
		}

		/**
		 * Registers all command fields from a class. It is recommended to only use this method in command constant classes. (i.e. Classes containing mostly, if not all command type constant fields)
		 * <p>All fields to be registered <b>MUST</b> be static.</p>
		 *
		 * @param commandsClass - The {@link Class} to lookup command fields to register.
		 * @return This builder.
		 */
		public CommandHandlerBuilder registerCommands(Class<?> commandsClass) {
			Field[] fields = commandsClass.getFields();
			for (Field field : fields) {
				try {
					if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
						field.setAccessible(true);
						Object object = field.get(null);
						if (object instanceof Command) {
							this.registerCommand((Command) object, field.getAnnotation(Aliases.class), field.getAnnotation(Permissions.class));
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return this;
		}


		/**
		 * Sets a {@link FeedbackHandlerBuilder} for the {@link CommandHandler}.
		 *
		 * @param feedbackBuilder The {@link FeedbackHandler} to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setFeedbackBuilder(FeedbackHandlerBuilder feedbackBuilder) {
			this.feedbackHandlerBuilder = feedbackBuilder;
			return this;
		}

		/**
		 * Sets the {@link ExecutorService} for the {@link CommandHandler}.
		 *
		 * @param executorService The {@link ExecutorService} to set.
		 * @return This builder.
		 */
		public CommandHandlerBuilder setExecutorService(ExecutorService executorService) {
			this.executorService = executorService;
			return this;
		}

		/**
		 * @return Returns the built {@link CommandHandler}.
		 */
		public CommandHandler build() {
			CommandHandler commandHandler = new CommandHandler(CommandPropertyMap.create(this.commandPropertyMap), this.prefixFunction, this.feedbackHandlerBuilder, this.executorService);
			commandHandler.aliasMap.putAll(this.aliasMap);
			return commandHandler;
		}
	}
}