package net.smelly.disparser;

import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.concurrent.Immutable;

/**
 * A wrapper class for an {@link Argument} that stores an argument configured with a name and description. This class also contains a boolean for determining if the argument is optional.
 * <p>This is used in {@link Command} for describing the command's arguments. The benefit of the system is to not have to create a new {@link Argument} for defining custom information about it.</p>
 *
 * @param <T> The type of the {@link Argument} result.
 * @author Luke Tonon
 * @see Argument
 * @see CommandMessage
 * @see Command
 */
@Immutable
public class ConfiguredArgument<T> {
	private final Argument<T> argument;
	private final CommandMessage name;
	private final CommandMessage description;
	private final boolean optional;

	public ConfiguredArgument(Argument<T> argument, CommandMessage name, CommandMessage description, boolean optional) {
		this.argument = argument;
		this.name = name;
		this.description = description;
		this.optional = optional;
	}

	public ConfiguredArgument(Argument<T> argument, CommandMessage name, CommandMessage description) {
		this(argument, name, description, false);
	}

	public ConfiguredArgument(Argument<T> argument, CommandMessage name) {
		this(argument, name, CommandMessage.EMPTY, false);
	}

	/**
	 * Creates a new {@link ConfiguredArgument} for a given {@link Argument} and name.
	 * @param argument An {@link Argument} to configure.
	 * @param name A {@link CommandMessage} to configure as the name of the given {@link Argument}.
	 * @param <T> The type of the {@link Argument} result.
	 * @return A new {@link ConfiguredArgument} for a given {@link Argument} and name.
	 */
	public static <T> ConfiguredArgument<T> named(Argument<T> argument, CommandMessage name) {
		return new ConfiguredArgument<>(argument, name);
	}

	/**
	 * Creates a new optional {@link ConfiguredArgument} for a given {@link Argument} and name.
	 * @param argument An {@link Argument} to configure.
	 * @param name A {@link CommandMessage} to configure as the name of the given {@link Argument}.
	 * @param <T> The type of the {@link Argument} result.
	 * @return A new optional {@link ConfiguredArgument} for a given {@link Argument} and name.
	 */
	public static <T> ConfiguredArgument<T> namedOptional(Argument<T> argument, CommandMessage name) {
		return new ConfiguredArgument<>(argument, name, CommandMessage.EMPTY, true);
	}

	/**
	 * Creates a new optional {@link ConfiguredArgument} for a given {@link ConfiguredArgument}.
	 * @param configuredArgument A {@link ConfiguredArgument} to create an optional instance for.
	 * @param <T> The type of the {@link Argument} result.
	 * @return A new optional {@link ConfiguredArgument} for a given {@link ConfiguredArgument}.
	 */
	public static <T> ConfiguredArgument<T> optional(ConfiguredArgument<T> configuredArgument) {
		return new ConfiguredArgument<>(configuredArgument.argument, configuredArgument.name, configuredArgument.description, configuredArgument.optional);
	}

	/**
	 * Parses the {@link Argument} in this {@link ConfiguredArgument} for a given {@link MessageReader}.
	 *
	 * @param reader A {@link MessageReader} to parse the {@link Argument} from.
	 * @return A {@link ParsedArgument} from the {@link Argument} being parsed.
	 * @throws CommandSyntaxException If an error occurs parsing the {@link Argument}.
	 * @see Argument#parse(MessageReader)
	 */
	public final ParsedArgument<T> parse(MessageReader reader) throws CommandSyntaxException {
		return this.argument.parse(reader);
	}

	/**
	 * @return The {@link Argument} belonging to this {@link ConfiguredArgument}.
	 */
	public Argument<T> getArgument() {
		return this.argument;
	}

	/**
	 * @return The {@link CommandMessage} representing the name of the {@link Argument} belonging to this {@link ConfiguredArgument}.
	 */
	public CommandMessage getName() {
		return this.name;
	}

	/**
	 * @return The {@link CommandMessage} representing the description of the {@link Argument} belonging to this {@link ConfiguredArgument}.
	 */
	public CommandMessage getDescription() {
		return this.description;
	}

	/**
	 * @return If the {@link Argument} belonging to this {@link ConfiguredArgument} should be treated as optional.
	 */
	public boolean isOptional() {
		return this.optional;
	}
}
