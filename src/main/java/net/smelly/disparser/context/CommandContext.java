package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.annotations.NullWhenErrored;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A class that works as a wrapper around an event that's used in executing commands.
 * <p>The event is the 'source' of the command execution.</p>
 * <p>An instance of this class should never be reused when executing a command as the {@link CommandContext#parsedArguments} in this class can directly relate to a command's arguments.</p>
 *
 * @param <E> The type of the event.
 * @author Luke Tonon
 * @see FeedbackHandler
 * @see BuiltInExceptionProvider
 * @see Command
 * @see ParsedArgument
 * @see ConfiguredArgument
 * @see Command#processCommand(CommandContext)
 * @see MessageCommandContext
 * @see PrivateMessageCommandContext
 * @see GuildMessageCommandContext
 */
public class CommandContext<E extends Event> {
	protected final List<ParsedArgument<?>> parsedArguments;
	protected final FeedbackHandler feedbackHandler;
	protected final BuiltInExceptionProvider exceptionProvider;
	protected final E event;

	public CommandContext(E event, List<ParsedArgument<?>> parsedArguments, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		this.event = event;
		this.parsedArguments = parsedArguments;
		this.feedbackHandler = feedbackHandler;
		this.exceptionProvider = exceptionProvider;
	}

	/**
	 * Takes in any {@link CommandContext} and parses a {@link List} of {@link ConfiguredArgument}s to it from a {@link Message}.
	 * <p>If an error occurs, then an exception will created by the context's {@link CommandContext#exceptionProvider} and be sent to the context's {@link CommandContext#feedbackHandler}, subsequently returning {@link Optional#empty()}</p>
	 *
	 * @param commandContext   A {@link CommandContext} to parse arguments for.
	 * @param message          A {@link Message} to parse the arguments from.
	 * @param commandArguments A {@link List} of {@link ConfiguredArgument}s to parse.
	 * @param <E>              The type of {@link Event}.
	 * @param <C>              The type of {@link CommandContext}.
	 * @return {@link Optional#empty()} if an error occurred trying to parse the arguments, otherwise an {@link Optional} containing the passed in {@link CommandContext}.
	 */
	public static <E extends Event, C extends CommandContext<E>> Optional<C> disparseArguments(C commandContext, Message message, List<ConfiguredArgument<?>> commandArguments) {
		if (commandArguments.size() > 0) {
			BuiltInExceptionProvider exceptionProvider = commandContext.getExceptionProvider();
			MessageReader reader = MessageReader.create(exceptionProvider, message);
			FeedbackHandler feedbackHandler = commandContext.getFeedbackHandler();
			int mandatorySize = getMandatorySize(commandArguments);
			if (testForPresentArgs(reader, feedbackHandler, commandArguments, mandatorySize)) {
				List<ParsedArgument<?>> parsedArguments = commandContext.getParsedArguments();
				if (mandatorySize != commandArguments.size()) {
					for (ConfiguredArgument<?> argument : commandArguments) {
						if (argument.isOptional()) {
							ParsedArgument<?> parsedArg = reader.tryToParseArgument(argument.getArgument());
							parsedArguments.add(parsedArg);
						} else {
							if (!reader.hasNextArg()) {
								feedbackHandler.sendError(exceptionProvider.getMissingArgumentException().create(argument.getName().getMessage(message.getChannel())));
								return Optional.empty();
							}
							try {
								parsedArguments.add(argument.parse(reader));
							} catch (Exception exception) {
								feedbackHandler.sendError(exception);
								return Optional.empty();
							}
						}
					}
				} else {
					for (ConfiguredArgument<?> argument : commandArguments) {
						try {
							parsedArguments.add(argument.parse(reader));
						} catch (Exception exception) {
							feedbackHandler.sendError(exception);
							return Optional.empty();
						}
					}
				}
			} else {
				return Optional.empty();
			}
		}

		return Optional.of(commandContext);
	}

	/**
	 * Tests to check if all the command's arguments are present in the message.
	 * <p>If an error occurs when testing for the arguments then that error will be sent to the {@link FeedbackHandler} using {@link FeedbackHandler#sendError(Exception)}</p>
	 *
	 * @param reader           A {@link MessageReader} to read the arguments.
	 * @param feedbackHandler  A {@link FeedbackHandler} to send the errors to.
	 * @param commandArguments The list of {@link ConfiguredArgument}s for a command.
	 * @return True if all mandatory arguments are present and false if otherwise.
	 */
	public static boolean testForPresentArgs(MessageReader reader, FeedbackHandler feedbackHandler, List<ConfiguredArgument<?>> commandArguments, int mandatorySize) {
		int readerArgumentLength = reader.getLength() - 1;
		BuiltInExceptionProvider exceptionProvider = reader.getExceptionProvider();
		if (readerArgumentLength < mandatorySize) {
			if (readerArgumentLength == 0) {
				feedbackHandler.sendError(exceptionProvider.getNoArgumentsException().create());
			} else {
				feedbackHandler.sendError(exceptionProvider.getMissingArgumentsException().create(mandatorySize, commandArguments.size() - mandatorySize));
			}
			return false;
		}
		return true;
	}

	/**
	 * @param arguments A {@link List} of {@link ConfiguredArgument}s to filter.
	 * @return A filtered list of arguments that aren't optional.
	 */
	public static int getMandatorySize(List<ConfiguredArgument<?>> arguments) {
		return (int) arguments.stream().filter(argument -> !argument.isOptional()).count();
	}

	/**
	 * @return This context's {@link #parsedArguments}.
	 */
	public List<ParsedArgument<?>> getParsedArguments() {
		return this.parsedArguments;
	}

	/**
	 * @return This context's {@link #feedbackHandler}.
	 */
	public FeedbackHandler getFeedbackHandler() {
		return this.feedbackHandler;
	}

	/**
	 * @return This context's {@link #exceptionProvider}.
	 */
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * @return This context's {@link #event}, or in other words, the 'source'.
	 */
	public final E getEvent() {
		return this.event;
	}

	/**
	 * Gets a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 * The list of {@link #parsedArguments} matches the {@link Command#getArguments()} for this {@link CommandContext}.
	 *
	 * @param argument The index of the argument.
	 * @param <A>      The type of the {@link ParsedArgument}.
	 * @return The {@link ParsedArgument} for an index.
	 */
	@SuppressWarnings("unchecked")
	public <A> ParsedArgument<A> getParsedArgument(int argument) {
		return (ParsedArgument<A>) this.parsedArguments.get(argument);
	}

	/**
	 * Gets the {@link ParsedArgument#getResult()} for a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 *
	 * @param argument The index of the {@link ParsedArgument} to get its {@link ParsedArgument#getResult()}.
	 * @param <A>      The type of the {@link ParsedArgument#getResult()}.
	 * @return - The {@link ParsedArgument#getResult()} for an index.
	 * @see #getParsedArgument(int)
	 */
	@NullWhenErrored
	public <A> A getParsedResult(int argument) {
		ParsedArgument<A> parsedArgument = this.getParsedArgument(argument);
		return parsedArgument != null ? parsedArgument.getResult() : null;
	}

	/**
	 * Gets the {@link ParsedArgument#getResult()} for a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 * If the result of {@link ParsedArgument#getResult()} is null then it will return {@param other}.
	 * This should only be used for optional arguments or more specifically, arguments that have {@link ConfiguredArgument#isOptional()} return true.
	 *
	 * @param argument The index of the {@link ParsedArgument#getResult()}.
	 * @param other    The result to return if {@link ParsedArgument#getResult()} is null.
	 * @param <A>      The type of the {@link ParsedArgument#getResult()}.
	 * @return The {@link ParsedArgument#getResult()} for an index or if it's not present then returns the other result.
	 * @see #getParsedArgument(int)
	 */
	@SuppressWarnings("unchecked")
	public <A> A getParsedResultOrElse(int argument, A other) {
		return (A) this.getParsedArgument(argument).getOrOtherResult(other);
	}

	/**
	 * Checks if a {@link ParsedArgument#getResult()} for an index is present and then accepts a consumer on that result.
	 *
	 * @param argument The index of the {@link ParsedArgument#getResult()}.
	 * @param consumer The consumer to accept on the result if it's present.
	 * @param <A>      The type of the {@link ParsedArgument#getResult()}.
	 */
	@SuppressWarnings("unchecked")
	public <A> void ifParsedResultPresent(int argument, Consumer<A> consumer) {
		((ParsedArgument<A>) this.getParsedArgument(argument)).ifHasResult(consumer);
	}
}
