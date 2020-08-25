package disparser;

import disparser.annotations.NullWhenErrored;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	private final ArgumentReader reader;
	private final List<ParsedArgument<?>> parsedArguments;
	
	private CommandContext(GuildMessageReceivedEvent event, ArgumentReader reader, List<ParsedArgument<?>> parsedArguments) {
		this.event = event;
		this.reader = reader;
		this.parsedArguments = parsedArguments;
	}
	
	/**
	 * Parses arguments with an {@link ArgumentReader} to create a new {@link CommandContext} instance.
	 * When a parsing error occurs it will send a message to the reader's channel {@link ArgumentReader#getChannel()}.
	 *
	 * @param event - The {@link GuildMessageReceivedEvent} that the message was sent from.
	 * @param command - The {@link Command} to create the context for.
	 * @param reader - The {@link ArgumentReader} to parse the arguments.
	 * @return An {@link Optional} {@link CommandContext} made from {@link Argument}s, empty if an error occurs when parsing the arguments
	 */
	public static Optional<CommandContext> createContext(final GuildMessageReceivedEvent event, final Command command, final ArgumentReader reader) {
		Member member = event.getMember();
		if (member == null) return Optional.empty();

		if (!command.hasPermissions(member)) {
			event.getChannel().sendMessage(MessageUtil.createErrorMessage("You do not have permission to run this command!")).queue();
			return Optional.empty();
		}
		
		List<Argument<?>> commandArguments = command.getArguments();
		if (commandArguments.size() > 0) {
			boolean hasOptionalArguments = !getOptionalArguments(commandArguments).isEmpty();
			if (testForPresentArgs(reader, commandArguments, hasOptionalArguments)) {
				List<ParsedArgument<?>> parsedArguments = new ArrayList<>(commandArguments.size());
				if (hasOptionalArguments) {
					for (int i = 0; i < commandArguments.size(); i++) {
						Argument<?> argument = commandArguments.get(i);
						if (argument.isOptional()) {
							ParsedArgument<?> parsedArg = reader.tryToParseArgument(argument);
							parsedArguments.add(parsedArg);
						} else {
							if (!reader.hasNextArg()) {
								int nextArg = i + 1;
								reader.getChannel().sendMessage(MessageUtil.createErrorMessage(nextArg + MessageUtil.getOrdinalForInteger(nextArg) + " argument is missing")).queue();
								return Optional.empty();
							}
							ParsedArgument<?> parsedArg = argument.parse(reader);
							String errorMessage = parsedArg.getErrorMessage();
							if (errorMessage != null) {
								reader.getChannel().sendMessage(MessageUtil.createErrorMessage(errorMessage)).queue();
								return Optional.empty();
							}
							parsedArguments.add(parsedArg);
						}
					}
				} else {
					for (Argument<?> argument : commandArguments) {
						ParsedArgument<?> parsedArg = argument.parse(reader);
						String errorMessage = parsedArg.getErrorMessage();
						if (errorMessage != null) {
							reader.getChannel().sendMessage(MessageUtil.createErrorMessage(errorMessage)).queue();
							return Optional.empty();
						}
						parsedArguments.add(parsedArg);
					}
				}
				return Optional.of(new CommandContext(event, reader, parsedArguments));
			}
			return Optional.empty();
		}
		return Optional.of(new CommandContext(event, reader, new ArrayList<>()));
	}
	
	/**
	 * Tests to check if all the command's arguments are present in the message.
	 * A message will be sent to the reader's channel {@link ArgumentReader#getChannel()} if an argument or multiple arguments are missing.
	 * 
	 * @param reader - The {@link ArgumentReader} to read the arguments.
	 * @param commandArguments - The list of {@link Argument}s for a command.
	 * @param hasOptionalArguments - If the command has {@link disparser.annotations.Optional} {@link Argument}s.
	 * @return True if no arguments and false if an argument or multiple arguments are missing
	 */
	public static boolean testForPresentArgs(final ArgumentReader reader, final List<Argument<?>> commandArguments, boolean hasOptionalArguments) {
		int readerArgumentLength = reader.getMessageComponents().length - 1;
		int commandArgumentsSize = commandArguments.size();
		
		if (hasOptionalArguments) {
			List<Argument<?>> optionalArguments = getOptionalArguments(commandArguments);
			int mandatorySize = commandArgumentsSize - optionalArguments.size();
			if (readerArgumentLength < mandatorySize) {
				TextChannel channel = reader.getChannel();
				if (readerArgumentLength == 0) {
					channel.sendMessage(MessageUtil.createErrorMessage("No arguments are present")).queue();
					return false;
				}
				
				if (readerArgumentLength - mandatorySize < -1) {
					channel.sendMessage(MessageUtil.createErrorMessage("More than one argument is missing, view this command's arguments")).queue();
				} else {
					channel.sendMessage(MessageUtil.createErrorMessage("Last argument is missing")).queue();
				}
				return false;
			}
		} else {
			if (readerArgumentLength < commandArgumentsSize) {
				TextChannel channel = reader.getChannel();
				if (readerArgumentLength == 0) {
					channel.sendMessage(MessageUtil.createErrorMessage("No arguments are present")).queue();
					return false;
				}
				
				List<String> missingArgs = new ArrayList<>(commandArgumentsSize - readerArgumentLength);
				for (int i = readerArgumentLength; i < commandArgumentsSize; i++) {
					missingArgs.add((i + 1) + MessageUtil.getOrdinalForInteger((i + 1)));
				}
				
				if (missingArgs.size() > 1) {
					channel.sendMessage(MessageUtil.createErrorMessage(MessageUtil.createFormattedSentenceOfCollection(missingArgs) + " arguments are missing")).queue();
				} else {
					channel.sendMessage(MessageUtil.createErrorMessage(missingArgs.get(0) + " argument is missing")).queue();
				}
				return false;
			}
		}
		return true;
	}
	
	public static List<Argument<?>> getOptionalArguments(List<Argument<?>> arguments) {
		return arguments.stream().filter(Argument::isOptional).collect(Collectors.toList());
	}
	
	public GuildMessageReceivedEvent getEvent() {
		return this.event;
	}

	public ArgumentReader getArgumentReader() {
		return this.reader;
	}
	
	@SuppressWarnings("unchecked")
	public <A> ParsedArgument<A> getParsedArgument(int argument) {
		return (ParsedArgument<A>) this.parsedArguments.get(argument);
	}
	
	@NullWhenErrored
	public <A> A getParsedResult(int argument) {
		ParsedArgument<A> parsedArgument = this.getParsedArgument(argument);
		return parsedArgument != null ? parsedArgument.getResult() : null;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A getParsedResultOrElse(int argument, A other) {
		return (A) this.getParsedArgument(argument).getOrOtherResult(other);
	}
	
	@SuppressWarnings("unchecked")
	public <A> void ifParsedResultPresent(int argument, Consumer<A> consumer) {
		((ParsedArgument<A>) this.getParsedArgument(argument)).ifHasResult(consumer);
	}
}