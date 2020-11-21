package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.BiFunction;

/**
 * This class contains a {@link BiFunction} for creating a {@link CommandException}.
 * The function takes in two generic type objects and uses them to create a {@link CommandMessage} to be used for creating a {@link CommandException}.
 * This class can store the two generic objects internally to be re-used for creating a {@link CommandException}.
 * <p> Simply put, this class is a double version of {@link DynamicCommandExceptionCreator}. </p>
 *
 * @author Luke Tonon
 * @see DynamicCommandExceptionCreator
 */
@ThreadSafe
public class BiDynamicCommandExceptionCreator<T, U> implements ExceptionCreator<CommandException> {
	private final BiFunction<T, U, CommandMessage> function;
	@Nullable
	private final T first;
	@Nullable
	private final U second;

	public BiDynamicCommandExceptionCreator(BiFunction<T, U, CommandMessage> function) {
		this(null, null, function);
	}

	public BiDynamicCommandExceptionCreator(@Nullable T first, @Nullable U second, BiFunction<T, U, CommandMessage> function) {
		this.first = first;
		this.second = second;
		this.function = function;
	}

	public static <T, U> BiDynamicCommandExceptionCreator<T, U> createInstance(BiFunction<T, U, CommandMessage> function) {
		return new BiDynamicCommandExceptionCreator<>(function);
	}

	@Override
	public CommandException create() {
		return new CommandException(this.function.apply(this.first, this.second));
	}

	public CommandException create(T first, U second) {
		return new CommandException(this.function.apply(first, second));
	}
}
