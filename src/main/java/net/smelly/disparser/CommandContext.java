package net.smelly.disparser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.smelly.disparser.annotations.NullWhenErrored;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.FeedbackHandlerBuilder;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProviderBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Holds an {@link ArgumentReader} for reading arguments of a message a list of parsed arguments, and the {@link GuildMessageReceivedEvent} that the command was sent from.
 * <p> Use this for processing commands in {@link Command#processCommand(CommandContext)}. </p>
 *
 * @author Luke Tonon
 */
public class CommandContext {
	private final GuildMessageReceivedEvent event;
	private final List<ParsedArgument<?>> parsedArguments;
	private final FeedbackHandler feedbackHandler;
	private final BuiltInExceptionProvider exceptionProvider;
	private final ArgumentReader reader;

	private CommandContext(GuildMessageReceivedEvent event, List<ParsedArgument<?>> parsedArguments, FeedbackHandlerBuilder feedbackHandlerBuilder, BuiltInExceptionProviderBuilder exceptionProviderBuilder) {
		this.event = event;
		this.parsedArguments = parsedArguments;
		this.feedbackHandler = feedbackHandlerBuilder.build(event.getChannel());
		this.exceptionProvider = exceptionProviderBuilder.build(event.getChannel());
		this.reader = ArgumentReader.create(this.feedbackHandler, this.exceptionProvider, event.getMessage());
	}

	/**
	 * Creates an {@link Optional} {@link CommandContext} for a {@link Command} with a {@link FeedbackHandlerBuilder} to use for building a {@link FeedbackHandler} for sending parsing feedback.
	 * If an error occurs when parsing the {@link CommandContext} for the command this will return an empty optional.
	 *
	 * @param event                  The {@link GuildMessageReceivedEvent} to use for parsing the command's arguments.
	 * @param command                The {@link Command} to try to parse and create an {@link CommandContext} for.
	 * @param feedbackHandlerBuilder The {@link FeedbackHandlerBuilder} to use for building a {@link FeedbackHandler} for sending feedback.
	 * @param exceptionProviderBuilder The {@link BuiltInExceptionProviderBuilder} to use for building a {@link BuiltInExceptionProvider} for throwing built-in exceptions.
	 * @return An {@link Optional} {@link CommandContext} made for a {@link Command}, empty if an error occurs when parsing the arguments.
	 */
	public static Optional<CommandContext> create(final GuildMessageReceivedEvent event, final Command command, final Set<Permission> permissions, final FeedbackHandlerBuilder feedbackHandlerBuilder, final BuiltInExceptionProviderBuilder exceptionProviderBuilder) {
		CommandContext commandContext = new CommandContext(event, new ArrayList<>(), feedbackHandlerBuilder, exceptionProviderBuilder);
		FeedbackHandler feedbackHandler = commandContext.getFeedbackHandler();

		Member member = event.getMember();
		if (member == null) return Optional.empty();

		BuiltInExceptionProvider builtInExceptionProvider = commandContext.exceptionProvider;
		if (!member.hasPermission(permissions)) {
			feedbackHandler.sendError(builtInExceptionProvider.getMissingPermissionsException().create(permissions));
			return Optional.empty();
		}

		List<Argument<?>> commandArguments = command.getArguments();
		if (commandArguments.size() > 0) {
			boolean hasOptionalArguments = !getOptionalArguments(commandArguments).isEmpty();
			ArgumentReader reader = commandContext.getArgumentReader();
			if (testForPresentArgs(reader, commandArguments, hasOptionalArguments)) {
				List<ParsedArgument<?>> parsedArguments = commandContext.parsedArguments;
				if (hasOptionalArguments) {
					for (int i = 0; i < commandArguments.size(); i++) {
						Argument<?> argument = commandArguments.get(i);
						if (argument.isOptional()) {
							ParsedArgument<?> parsedArg = reader.tryToParseArgument(argument);
							parsedArguments.add(parsedArg);
						} else {
							if (!reader.hasNextArg()) {
								feedbackHandler.sendError(builtInExceptionProvider.getSpecificMissingArgumentException().create(i + 1));
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
	 * Creates an {@link Optional} {@link CommandContext} using the {@link #create(GuildMessageReceivedEvent, Command, Set, FeedbackHandlerBuilder, BuiltInExceptionProviderBuilder)} method.
	 * First this method will try to find a matching command for a {@link CommandHandler} and then process that command.
	 * The term "disparse" is used here since it performs all of Disparser's core parsing and processing actions for a command in one method.
	 *
	 * @param event The {@link GuildMessageReceivedEvent} to use for parsing the command's arguments.
	 * @return An {@link Optional} {@link CommandContext} made for a {@link Command}, empty if an error occurs when parsing the arguments.
	 * @see #create(GuildMessageReceivedEvent, Command, Set, FeedbackHandlerBuilder, BuiltInExceptionProviderBuilder).
	 */
	public static Optional<CommandContext> createAndDisparse(final CommandHandler commandHandler, final GuildMessageReceivedEvent event) {
		String firstComponent = event.getMessage().getContentRaw().split(" ")[0];
		String prefix = commandHandler.getPrefix(event.getGuild());
		if (firstComponent.startsWith(prefix)) {
			Command command = commandHandler.aliasMap.get(firstComponent.substring(prefix.length()));
			if (command != null) {
				Optional<CommandContext> commandContext = create(event, command, commandHandler.getPermissions(command), commandHandler.getFeedbackHandlerBuilder(), commandHandler.getExceptionProviderBuilder());
				commandContext.ifPresent(context -> {
					try {
						command.processCommand(context);
					} catch (Exception exception) {
						context.getFeedbackHandler().sendError(exception);
						exception.printStackTrace();
					}
				});
				return commandContext;
			}
		}
		return Optional.empty();
	}

	/**
	 * Tests to check if all the command's arguments are present in the message.
	 * A message will be sent to the reader's channel {@link ArgumentReader#getChannel()} if an argument or multiple arguments are missing.
	 *
	 * @param reader               - The {@link ArgumentReader} to read the arguments.
	 * @param commandArguments     - The list of {@link Argument}s for a command.
	 * @param hasOptionalArguments - If the command has {@link net.smelly.disparser.annotations.Optional} {@link Argument}s.
	 * @return True if no arguments and false if an argument or multiple arguments are missing
	 */
	public static boolean testForPresentArgs(final ArgumentReader reader, final List<Argument<?>> commandArguments, boolean hasOptionalArguments) {
		int readerArgumentLength = reader.getMessageComponents().length - 1;
		int commandArgumentsSize = commandArguments.size();

		FeedbackHandler feedbackHandler = reader.getFeedbackHandler();
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
	 * @param arguments - A list of arguments to filter.
	 * @return A filtered optional list of arguments.
	 */
	public static List<Argument<?>> getOptionalArguments(List<Argument<?>> arguments) {
		return arguments.stream().filter(Argument::isOptional).collect(Collectors.toList());
	}

	/**
	 * @return The {@link GuildMessageReceivedEvent} the message creating this {@link CommandContext} was sent from.
	 */
	public GuildMessageReceivedEvent getEvent() {
		return this.event;
	}

	/**
	 * @return The {@link ArgumentReader} for this {@link CommandContext}.
	 */
	public ArgumentReader getArgumentReader() {
		return this.reader;
	}

	/**
	 * Use this to get the {@link FeedbackHandler} for this {@link CommandContext} to send feedback when processing commands.
	 *
	 * @return The {@link FeedbackHandler} for this {@link CommandContext}.
	 * @see FeedbackHandler
	 */
	public FeedbackHandler getFeedbackHandler() {
		return this.feedbackHandler;
	}

	/**
	 * Use this to get the {@link BuiltInExceptionProvider} for this {@link CommandContext} to throw built-in exceptions when processing commands.
	 *
	 * @return The {@link BuiltInExceptionProvider} for this {@link CommandContext}.
	 * @see BuiltInExceptionProvider
	 */
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * Gets a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 * The list of {@link #parsedArguments} matches the {@link Command#getArguments()} for this {@link CommandContext}.
	 *
	 * @param argument - The index of the argument.
	 * @param <A>      - The type of the {@link ParsedArgument}.
	 * @return The {@link ParsedArgument} for an index.
	 */
	@SuppressWarnings("unchecked")
	public <A> ParsedArgument<A> getParsedArgument(int argument) {
		return (ParsedArgument<A>) this.parsedArguments.get(argument);
	}

	/**
	 * Gets the {@link ParsedArgument#getResult()} for a {@link ParsedArgument} for this {@link CommandContext} by an index.
	 *
	 * @param argument - The index of the {@link ParsedArgument} to get its {@link ParsedArgument#getResult()}.
	 * @param <A>      - The type of the {@link ParsedArgument#getResult()}.
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
	 * @param argument - The index of the {@link ParsedArgument#getResult()}.
	 * @param other    - The result to return if {@link ParsedArgument#getResult()} is null.
	 * @param <A>      - The type of the {@link ParsedArgument#getResult()}.
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
	 * @param argument - The index of the {@link ParsedArgument#getResult()}.
	 * @param consumer - The consumer to accept on the result if it's present.
	 * @param <A>      - The type of the {@link ParsedArgument#getResult()}.
	 */
	@SuppressWarnings("unchecked")
	public <A> void ifParsedResultPresent(int argument, Consumer<A> consumer) {
		((ParsedArgument<A>) this.getParsedArgument(argument)).ifHasResult(consumer);
	}
}