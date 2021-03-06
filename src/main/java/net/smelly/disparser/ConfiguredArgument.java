package net.smelly.disparser;

import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * A wrapper class for an {@link Argument} that stores an argument configured with a name and description.
 * <p>The benefit of the system is to not have to create a new {@link Argument} for defining custom information about it.</p>
 *
 * @param <T> The type of the {@link Argument} result.
 * @author Luke Tonon
 * @see Argument
 * @see CommandMessage
 * @see Command
 */
@Immutable
public final class ConfiguredArgument<T> {
	private final Argument<T> argument;
	private final CommandMessage name;
	private final CommandMessage description;

	public ConfiguredArgument(Argument<T> argument, CommandMessage name, CommandMessage description) {
		this.argument = argument;
		this.name = name;
		this.description = description;
	}

	public ConfiguredArgument(Argument<T> argument, CommandMessage name) {
		this(argument, name, CommandMessage.EMPTY);
	}

	/**
	 * Creates a new {@link ConfiguredArgument} for a given {@link Argument} and name.
	 *
	 * @param argument An {@link Argument} to configure.
	 * @param name     A {@link CommandMessage} to configure as the name of the given {@link Argument}.
	 * @param <T>      The type of the {@link Argument} result.
	 * @return A new {@link ConfiguredArgument} for a given {@link Argument} and name.
	 */
	public static <T> ConfiguredArgument<T> named(Argument<T> argument, CommandMessage name) {
		return new ConfiguredArgument<>(argument, name);
	}

	/**
	 * Parses the {@link Argument} in this {@link ConfiguredArgument} for a given {@link MessageReader}.
	 *
	 * @param reader A {@link MessageReader} to parse the {@link Argument} from.
	 * @return A {@link ParsedArgument} from the {@link Argument} being parsed.
	 * @throws CommandException If an error occurs parsing the {@link Argument}.
	 * @see Argument#parse(MessageReader)
	 */
	public ParsedArgument<T> parse(MessageReader reader) throws CommandException {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		ConfiguredArgument<?> that = (ConfiguredArgument<?>) o;
		return Objects.equals(this.argument, that.argument) && Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.argument, this.name);
	}

	@Override
	public String toString() {
		return "ConfiguredArgument{" +
				"argument=" + this.argument +
				", name=" + this.name +
				", description=" + this.description +
				'}';
	}
}
