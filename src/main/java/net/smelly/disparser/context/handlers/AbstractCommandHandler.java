package net.smelly.disparser.context.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.DisparserExceptionProvider;
import net.smelly.disparser.properties.CommandProperty;
import net.smelly.disparser.properties.CommandPropertyMap;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * An abstract class representing an implementation of a command dispatcher for an {@link Event}.
 * <p>Instances of this class are capable of getting commands by a name, storing properties for commands, having prefixes for an {@link Event}, sending feedback, creating built-in exceptions, and executing tasks with a {@link ExecutorService}</p>
 * <p>This class should only ever be extended for making new handlers for events that Disparser doesn't have built-in support for. For using the built-in handlers take a look at {@link CommandHandler}, {@link GuildCommandHandler}, and {@link PrivateCommandHandler}</p>
 *
 * @param <E> The type of the {@link Event} this handler dispatches for.
 * @param <C> The type of {@link CommandContext} this handler dispatchers for.
 * @author Luke Tonon
 * @see ListenerAdapter
 * @see Event
 * @see CommandContext
 * @see AbstractCommandHandlerBuilder
 * @see CommandHandler
 * @see GuildCommandHandler
 * @see PrivateCommandHandler
 */
@ThreadSafe
public abstract class AbstractCommandHandler<E extends Event, C extends CommandContext<E>> extends ListenerAdapter {
	protected final ConcurrentHashMap<String, Command<E, C>> aliasMap = new ConcurrentHashMap<>();
	protected final CommandPropertyMap<E, C> commandPropertyMap;
	protected final Function<E, String> prefixFunction;
	protected final Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction;
	protected final FeedbackHandlerBuilder feedbackHandlerBuilder;
	protected final ExecutorService executorService;

	public AbstractCommandHandler(CommandPropertyMap<E, C> commandPropertyMap, Function<E, String> prefixFunction, FeedbackHandlerBuilder feedbackHandlerBuilder, Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction, ExecutorService executorService) {
		this.commandPropertyMap = commandPropertyMap;
		this.prefixFunction = prefixFunction;
		this.feedbackHandlerBuilder = feedbackHandlerBuilder;
		this.exceptionProviderFunction = exceptionProviderFunction;
		this.executorService = executorService;
	}

	public AbstractCommandHandler(String prefix, FeedbackHandlerBuilder feedbackHandlerBuilder, Function<MessageChannel, BuiltInExceptionProvider> exceptionProviderFunction) {
		this(CommandPropertyMap.createEmpty(), guild -> prefix, feedbackHandlerBuilder, exceptionProviderFunction, Executors.newSingleThreadExecutor());
	}

	/**
	 * Registers a {@link Collection} of {@link Command}s.
	 *
	 * @param commands The {@link Collection} of {@link Command}s to register.
	 */
	protected void registerCommands(Collection<Command<E, C>> commands) {
		commands.forEach(this::registerCommand);
	}

	/**
	 * Registers a {@link Command}.
	 *
	 * @param command The {@link Command} to register.
	 */
	protected void registerCommand(Command<E, C> command) {
		this.commandPropertyMap.putCommand(command);
	}

	/**
	 * Registers a {@link Command} with a preset value map of its properties.
	 *
	 * @param command The {@link Command} to register.
	 * @param map     The {@link Map} to put for this {@link Command}'s properties.
	 */
	@SuppressWarnings("unchecked")
	protected void registerCommand(Command<E, C> command, Map<CommandProperty<?, ?>, CommandProperty.Value<?>> map) {
		this.aliasMap.entrySet().removeIf(entry -> entry.getValue() == command);
		for (String alias : (Set<String>) map.computeIfAbsent(command.getAliasesProperty(), (key) -> CommandProperty.Value.create(command.getAliasesProperty())).get()) {
			this.aliasMap.put(alias, command);
		}
		CommandPropertyMap.PropertyMap propertyMap = this.commandPropertyMap.getAndClearPropertyMap(command);
		propertyMap.putAll(map);
	}

	/**
	 * Gets this handler's {@link CommandPropertyMap}.
	 *
	 * @return This handler's {@link CommandPropertyMap}.
	 */
	public final CommandPropertyMap<E, C> getCommandPropertyMap() {
		return this.commandPropertyMap;
	}

	/**
	 * Gets the permissions for a given {@link Command}.
	 *
	 * @param command The {@link Command} to get the permissions for.
	 * @return The permissions for the given {@link Command}.
	 */
	public final UnmodifiableSet<Permission> getPermissions(Command<E, C> command) {
		return this.commandPropertyMap.getPropertyMap(command).get(command.getPermissionsProperty()).get();
	}

	/**
	 * Override this in your own {@link AbstractCommandHandler} extension if you wish to have the prefix be more dynamic and/or configurable.
	 *
	 * @param event The event belonging to the sent command.
	 * @return The prefix for the commands.
	 */
	public String getPrefix(E event) {
		return this.prefixFunction.apply(event);
	}

	/**
	 * Gets this handler's {@link FeedbackHandlerBuilder}.
	 * This is used for creating a {@link FeedbackHandler} to be used for sending feedback when processing commands.
	 * <p> This is {@link FeedbackHandlerBuilder#SIMPLE_BUILDER} by default. </p>
	 *
	 * @return The {@link FeedbackHandlerBuilder} for this {@link AbstractCommandHandler}.
	 */
	public final FeedbackHandlerBuilder getFeedbackHandlerBuilder() {
		return this.feedbackHandlerBuilder;
	}

	/**
	 * Gets this handler's {@link Function} for getting a {@link BuiltInExceptionProvider} for a {@link MessageChannel}.
	 * This is used for creating a {@link net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider} to be used for creating exceptions for a text channel.
	 * <p> This is {@link DisparserExceptionProvider#GETTER} by default. </p>
	 *
	 * @return The {@link Function} for getting a {@link BuiltInExceptionProvider} for a {@link MessageChannel} for this {@link AbstractCommandHandler}.
	 */
	public final Function<MessageChannel, BuiltInExceptionProvider> getExceptionProviderFunction() {
		return this.exceptionProviderFunction;
	}

	/**
	 * Shuts down this handler's {@link #executorService}.
	 */
	public void shutdown() {
		this.executorService.shutdown();
	}
}
