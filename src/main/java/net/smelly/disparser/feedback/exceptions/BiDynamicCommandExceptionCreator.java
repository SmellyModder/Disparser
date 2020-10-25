package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.BiFunction;

/**
 * This class contains a {@link BiFunction} for creating a {@link CommandSyntaxException}.
 * The function takes in two generic type objects and parses them to a string to be used for creating a message for a {@link CommandSyntaxException}.
 * This class can store the two generic objects internally to be re-used for creating a {@link CommandSyntaxException}.
 * <p> Simply put, this class is a bi version of {@link DynamicCommandExceptionCreator}. </p>
 *
 * @author Luke Tonon
 * @see DynamicCommandExceptionCreator
 */
@ThreadSafe
public class BiDynamicCommandExceptionCreator<T, U> implements CommandExceptionCreator<CommandSyntaxException> {
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
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.function.apply(this.first, this.second));
	}

	public CommandSyntaxException create(T first, U second) {
		return new CommandSyntaxException(this.function.apply(first, second));
	}
}
