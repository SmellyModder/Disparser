package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;

/**
 * This class contains a function for creating a {@link CommandException}.
 * The function takes in a generic type object and uses it to create a {@link CommandMessage} to be used for creating a {@link CommandException}.
 * This class can store an object internally to be re-used for creating a {@link CommandException}.
 *
 * @author Luke Tonon
 * @see ExceptionCreator
 */
@ThreadSafe
public class DynamicCommandExceptionCreator<T> implements ExceptionCreator<CommandException> {
	private final Function<T, CommandMessage> function;
	@Nullable
	private final T object;

	public DynamicCommandExceptionCreator(Function<T, CommandMessage> function) {
		this(null, function);
	}

	public DynamicCommandExceptionCreator(@Nullable T object, Function<T, CommandMessage> function) {
		this.object = object;
		this.function = function;
	}

	public static <T> DynamicCommandExceptionCreator<T> createInstance(Function<T, CommandMessage> function) {
		return new DynamicCommandExceptionCreator<>(function);
	}

	@Override
	public CommandException create() {
		return new CommandException(this.function.apply(this.object));
	}

	public CommandException create(T object) {
		return new CommandException(this.function.apply(object));
	}
}
