package net.smelly.disparser.context;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * A class that works as a wrapper around an event that's used in executing commands.
 * <p>The event is the 'source' of the command execution.</p>
 * <p>An instance of this class must be reused cautiously as the {@link CommandContext#parsedArguments} map is not thread-safe.</p>
 *
 * @param <E> The type of the event.
 * @author Luke Tonon
 * @see FeedbackHandler
 * @see BuiltInExceptionProvider
 * @see Command
 * @see ParsedArgument
 * @see ConfiguredArgument
 * @see Command#processCommand(CommandContext, ContextConsumer)
 * @see MessageCommandContext
 * @see PrivateMessageCommandContext
 * @see GuildMessageCommandContext
 */
@NotThreadSafe
public class CommandContext<E extends Event> {
	protected final List<ParsedArgument<?>> parsedArguments;
	protected final CommandPropertyMap.PropertyMap propertyMap;
	protected final FeedbackHandler feedbackHandler;
	protected final BuiltInExceptionProvider exceptionProvider;
	protected final E event;

	public CommandContext(E event, List<ParsedArgument<?>> parsedArguments, CommandPropertyMap.PropertyMap propertyMap, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		this.event = event;
		this.parsedArguments = parsedArguments;
		this.propertyMap = propertyMap;
		this.feedbackHandler = feedbackHandler;
		this.exceptionProvider = exceptionProvider;
	}

	/**
	 * Gets this context's {@link #parsedArguments}.
	 *
	 * @return This context's {@link #parsedArguments}.
	 */
	@Nonnull
	public List<ParsedArgument<?>> getParsedArguments() {
		return this.parsedArguments;
	}

	/**
	 * Gets this context's {@link CommandPropertyMap.PropertyMap}.
	 *
	 * @return This context's {@link #propertyMap}
	 */
	@Nonnull
	public CommandPropertyMap.PropertyMap getPropertyMap() {
		return this.propertyMap;
	}

	/**
	 * @return This context's {@link #feedbackHandler}.
	 */
	@Nonnull
	public FeedbackHandler getFeedbackHandler() {
		return this.feedbackHandler;
	}

	/**
	 * @return This context's {@link #exceptionProvider}.
	 */
	@Nonnull
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * @return This context's {@link #event}, or in other words, the 'source'.
	 */
	@Nonnull
	public final E getEvent() {
		return this.event;
	}

	/**
	 * Gets a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 *
	 * @param argument The index of the argument.
	 * @param <A>      The type of the {@link ParsedArgument}.
	 * @return The {@link ParsedArgument} for an index.
	 * @throws IndexOutOfBoundsException If there is no {@link ParsedArgument} at the given index.
	 * @throws ClassCastException   If the argument type at that index doesn't match the argument type specified.
	 */
	@Nonnull
	@SuppressWarnings("unchecked")
	public <A> ParsedArgument<A> getParsedArgument(int argument) {
		return (ParsedArgument<A>) this.parsedArguments.get(argument);
	}

	/**
	 * Gets the {@link ParsedArgument#getResult()} for a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 *
	 * @param argument The index of the {@link ParsedArgument} to get its {@link ParsedArgument#getResult()}.
	 * @param <A>      The type of the {@link ParsedArgument#getResult()}.
	 * @return The {@link ParsedArgument#getResult()} for an index.
	 * @throws IndexOutOfBoundsException If there is no {@link ParsedArgument} at the given index.
	 * @throws ClassCastException   If the value type at that index doesn't match the type specified.
	 * @see #getParsedArgument(int)
	 */
	@Nonnull
	public <A> A getParsedResult(int argument) {
		ParsedArgument<A> parsedArgument = this.getParsedArgument(argument);
		return parsedArgument.getResult();
	}
}
