package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.Command;
import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Context;
import net.smelly.disparser.annotations.Permissions;
import net.smelly.disparser.concurrent.DisparsingThreadFactory;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.DisparserExceptionProvider;
import net.smelly.disparser.properties.AliasesProperty;
import net.smelly.disparser.properties.CommandPropertyMap;
import net.smelly.disparser.properties.PermissionsProperty;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * An abstract builder class for an {@link AbstractCommandHandler}.
 * <p>{@link AbstractCommandHandler} has many features to it, and with that, some you might not want to change from their defaults. Because of that, simply making new instances of types extending {@link AbstractCommandHandler} can be overcomplicated, and this builder class helps reduce that issue.</p>
 *
 * @param <E> The type of {@link Event} for the {@link AbstractCommandHandler} this constructs.
 * @param <C> The type of {@link CommandContext} for the {@link AbstractCommandHandler} this constructs.
 * @param <H> The type of {@link AbstractCommandHandler} this builds.
 * @param <B> The type of this builder.
 * @author Luke Tonon
 * @see AbstractCommandHandler
 * @see CommandHandler.Builder
 * @see GuildCommandHandler.Builder
 * @see PrivateCommandHandler.Builder
 */
@NotThreadSafe
public abstract class AbstractCommandHandlerBuilder<E extends Event, C extends CommandContext<E>, H extends AbstractCommandHandler<E, C>, B extends AbstractCommandHandlerBuilder<E, C, H, B>> {
	protected final Map<String, Command<E, C>> aliasMap = new HashMap<>();
	protected final Map<Command<E, C>, CommandPropertyMap.PropertyMap> commandPropertyMap = new HashMap<>();
	protected Function<E, String> prefixFunction = event -> "!";
	protected FeedbackHandlerBuilder feedbackHandlerBuilder = FeedbackHandlerBuilder.SIMPLE_BUILDER;
	protected Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction = DisparserExceptionProvider.GETTER;
	protected ExecutorService executorService = Executors.newSingleThreadExecutor(new DisparsingThreadFactory("Default"));

	/**
	 * Registers a command for an alias.
	 *
	 * @param alias   The alias for this command.
	 * @param command The command to register.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B registerCommand(String alias, Command<E, C> command) {
		this.aliasMap.put(alias, command);
		return (B) this;
	}

	/**
	 * Registers a command by all its aliases.
	 *
	 * @param command The command to register.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B registerCommand(Command<E, C> command) {
		this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
		AliasesProperty aliasesProperty = command.getAliasesProperty();
		UnmodifiableSet<String> aliases = aliasesProperty.get(null);
		for (String alias : aliases) {
			this.aliasMap.put(alias, command);
		}
		this.commandPropertyMap.computeIfAbsent(command, (key) -> new CommandPropertyMap.PropertyMap()).putOrModify(aliasesProperty, aliases);
		return (B) this;
	}

	/**
	 * Registers a command with {@link Aliases} and {@link Permissions} annotations.
	 * <p>This method is simply a way to further customize the aliases and permissions of a command when you want to use an immutable command.</p>
	 *
	 * @param command     The command to register.
	 * @param aliases     The {@link Aliases} annotation to use with this command.
	 * @param permissions The {@link Permissions} annotation to use with this command.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B registerCommand(Command<E, C> command, @Nullable Aliases aliases, @Nullable Permissions permissions) {
		AliasesProperty aliasesProperty = command.getAliasesProperty();
		UnmodifiableSet<String> commandAliases = aliasesProperty.get(aliases);
		this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
		for (String alias : commandAliases) {
			this.aliasMap.put(alias, command);
		}
		PermissionsProperty permissionsProperty = command.getPermissionsProperty();
		this.commandPropertyMap.computeIfAbsent(command, (key) -> new CommandPropertyMap.PropertyMap()).putOrModify(aliasesProperty, commandAliases).putOrModify(permissionsProperty, permissionsProperty.get(permissions));
		return (B) this;
	}

	/**
	 * Registers multiple commands by their aliases.
	 *
	 * @param commands The commands to register.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final B registerCommands(Command<E, C>... commands) {
		for (Command<E, C> command : commands) {
			this.registerCommand(command);
		}
		return (B) this;
	}

	/**
	 * Registers all command fields from a class. It is recommended to only use this method in command constant classes. (i.e. Classes containing mostly, if not all command type constant fields)
	 * <p>All fields to be registered <b>MUST</b> be static.</p>
	 *
	 * @param commandsClass The {@link Class} to lookup command fields to register.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B registerCommands(Class<?> commandsClass) {
		Field[] fields = commandsClass.getFields();
		for (Field field : fields) {
			try {
				if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
					field.setAccessible(true);
					Object object = field.get(null);
					if (object instanceof Command<?, ?>) {
						Context context = field.getAnnotation(Context.class);
						if (context == null || this.getContextClass().isAssignableFrom(context.value())) {
							this.registerCommand((Command<E, C>) object, field.getAnnotation(Aliases.class), field.getAnnotation(Permissions.class));
						}
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return (B) this;
	}

	/**
	 * Sets a prefix for the {@link AbstractCommandHandler}.
	 *
	 * @param prefix The prefix to set.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B setPrefix(String prefix) {
		this.prefixFunction = (event) -> prefix;
		return (B) this;
	}

	/**
	 * Sets a prefix function for the {@link CommandHandler}.
	 *
	 * @param prefixFunction The prefix function to set.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B setPrefix(Function<E, String> prefixFunction) {
		this.prefixFunction = prefixFunction;
		return (B) this;
	}

	/**
	 * Sets a {@link FeedbackHandlerBuilder} for the {@link CommandHandler}.
	 *
	 * @param feedbackBuilder The {@link FeedbackHandlerBuilder} to set.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B setFeedbackBuilder(FeedbackHandlerBuilder feedbackBuilder) {
		this.feedbackHandlerBuilder = feedbackBuilder;
		return (B) this;
	}

	/**
	 * Sets a {@link Function} for getting a {@link BuiltInExceptionProvider} for a {@link MessageChannel} for the {@link CommandHandler}.
	 *
	 * @param exceptionProviderFunction The {@link Function} to set.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B setExceptionProviderBuilder(Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction) {
		this.exceptionProviderFunction = exceptionProviderFunction;
		return (B) this;
	}

	/**
	 * Sets the {@link ExecutorService} for the {@link CommandHandler}.
	 *
	 * @param executorService The {@link ExecutorService} to set.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
		return (B) this;
	}

	/**
	 * Gets the {@link Class} for the type of {@link CommandContext} for the type of {@link AbstractCommandHandler} this builds for.
	 * <p>This in itself might not seem to useful, but it's useful in a dynamic scenario for knowing what type of commands you can register.</p>
	 *
	 * @return The {@link Class} for the type of {@link CommandContext} for the type of {@link AbstractCommandHandler} this builds for.
	 */
	public abstract Class<C> getContextClass();

	/**
	 * Builds this builder.
	 *
	 * @return The constructed {@link AbstractCommandHandler} from this builder.
	 */
	public abstract H build();
}
