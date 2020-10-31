package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.Command;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.annotations.NullWhenErrored;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
	 * Takes in any {@link CommandContext} and parses a {@link List} of {@link Argument}s to it from a {@link Message}.
	 * <p>If an error occurs, then an exception will created by the context's {@link CommandContext#exceptionProvider} and be sent to the context's {@link CommandContext#feedbackHandler}, subsequently returning {@link Optional#empty()}</p>
	 *
	 * @param commandContext   A {@link CommandContext} to parse arguments for.
	 * @param message          A {@link Message} to parse the arguments from.
	 * @param commandArguments A {@link List} of {@link Argument}s to parse.
	 * @param <E>              The type of {@link Event}.
	 * @param <C>              The type of {@link CommandContext}.
	 * @return {@link Optional#empty()} if an error occurred trying to parse the arguments, otherwise an {@link Optional} containing the passed in {@link CommandContext}.
	 */
	public static <E extends Event, C extends CommandContext<E>> Optional<C> disparseArguments(C commandContext, Message message, List<Argument<?>> commandArguments) {
		if (commandArguments.size() > 0) {
			BuiltInExceptionProvider exceptionProvider = commandContext.getExceptionProvider();
			ArgumentReader reader = ArgumentReader.create(exceptionProvider, message);
			boolean hasOptionalArguments = !getOptionalArguments(commandArguments).isEmpty();
			FeedbackHandler feedbackHandler = commandContext.getFeedbackHandler();
			if (testForPresentArgs(reader, feedbackHandler, commandArguments, hasOptionalArguments)) {
				List<ParsedArgument<?>> parsedArguments = commandContext.getParsedArguments();
				if (hasOptionalArguments) {
					for (int i = 0; i < commandArguments.size(); i++) {
						Argument<?> argument = commandArguments.get(i);
						if (argument.isOptional()) {
							ParsedArgument<?> parsedArg = reader.tryToParseArgument(argument);
							parsedArguments.add(parsedArg);
						} else {
							if (!reader.hasNextArg()) {
								feedbackHandler.sendError(exceptionProvider.getSpecificMissingArgumentException().create(i + 1));
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
					for (Argument<?> argument : commandArguments) {
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
	 * @param reader               A {@link ArgumentReader} to read the arguments.
	 * @param feedbackHandler      A {@link FeedbackHandler} to send the errors to.
	 * @param commandArguments     The list of {@link Argument}s for a command.
	 * @param hasOptionalArguments If a command has at least one {@link Argument} that has {@link Argument#isOptional()} return true.
	 * @return True if all mandatory arguments are present and false if otherwise.
	 */
	public static boolean testForPresentArgs(ArgumentReader reader, FeedbackHandler feedbackHandler, List<Argument<?>> commandArguments, boolean hasOptionalArguments) {
		int readerArgumentLength = reader.getMessageComponents().length - 1;
		int commandArgumentsSize = commandArguments.size();

		BuiltInExceptionProvider exceptionProvider = reader.getExceptionProvider();
		if (hasOptionalArguments) {
			List<Argument<?>> optionalArguments = getOptionalArguments(commandArguments);
			int mandatorySize = commandArgumentsSize - optionalArguments.size();
			if (readerArgumentLength < mandatorySize) {
				if (readerArgumentLength == 0) {
					feedbackHandler.sendError(exceptionProvider.getNoArgumentsException().create());
					return false;
				}

				if (readerArgumentLength - mandatorySize < -1) {
					feedbackHandler.sendError(exceptionProvider.getMissingArgumentsException().create());
				} else {
					feedbackHandler.sendError(exceptionProvider.getMissingArgumentException().create());
				}
				return false;
			}
		} else {
			if (readerArgumentLength < commandArgumentsSize) {
				if (readerArgumentLength == 0) {
					feedbackHandler.sendError(exceptionProvider.getNoArgumentsException().create());
					return false;
				}

				List<Integer> missingArgs = new ArrayList<>(commandArgumentsSize - readerArgumentLength);
				for (int i = readerArgumentLength; i < commandArgumentsSize; i++) {
					missingArgs.add(i + 1);
				}

				if (missingArgs.size() > 1) {
					feedbackHandler.sendError(exceptionProvider.getSpecificMissingArgumentsException().create(missingArgs));
				} else {
					feedbackHandler.sendError(exceptionProvider.getSpecificMissingArgumentException().create(1));
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * @param arguments A {@link List} of {@link Argument}s to filter.
	 * @return A filtered list of arguments that are optional.
	 */
	public static List<Argument<?>> getOptionalArguments(List<Argument<?>> arguments) {
		return arguments.stream().filter(Argument::isOptional).collect(Collectors.toList());
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
	 * This should only be used for optional arguments. Moreover, arguments that have {@link Argument#isOptional()} return true.
	 *
	 * @param argument The index of the {@link ParsedArgument#getResult()}.
	 * @param other    The result to return if {@link ParsedArgument#getResult()} is null.
	 * @param <A>      The type of the {@link ParsedArgument#getResult()}.
	 * @return - The {@link ParsedArgument#getResult()} for an index or if it's not present then returns the other result.
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
