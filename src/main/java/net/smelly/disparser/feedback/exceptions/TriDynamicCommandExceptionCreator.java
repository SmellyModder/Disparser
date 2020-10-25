package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class contains a {@link TriFunction} for creating a {@link CommandSyntaxException}.
 * The function takes in three generic type objects and parses them to a string to be used for creating a message for a {@link CommandSyntaxException}.
 * This class can store the three generic objects internally to be re-used for creating a {@link CommandSyntaxException}.
 * <p> Simply put, this class is a tri version of {@link DynamicCommandExceptionCreator}. </p>
 *
 * @author Luke Tonon
 * @see DynamicCommandExceptionCreator
 */
@ThreadSafe
public class TriDynamicCommandExceptionCreator<T, U, V> implements CommandExceptionCreator<CommandSyntaxException> {
	private final TriFunction<T, U, V> function;
	@Nullable
	private final T first;
	@Nullable
	private final U second;
	@Nullable
	private final V third;

	public TriDynamicCommandExceptionCreator(TriFunction<T, U, V> function) {
		this(null, null, null, function);
	}

	public TriDynamicCommandExceptionCreator(@Nullable T first, @Nullable U second, @Nullable V third, TriFunction<T, U, V> function) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.function = function;
	}

	public static <T, U, V> TriDynamicCommandExceptionCreator<T, U, V> createInstance(TriFunction<T, U, V> function) {
		return new TriDynamicCommandExceptionCreator<>(function);
	}

	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.function.apply(this.first, this.second, this.third));
	}

	public CommandSyntaxException create(T first, U second, V third) {
		return new CommandSyntaxException(this.function.apply(first, second, third));
	}

	public interface TriFunction<T, U, V> {
		/**
		 * Applies this function to the given arguments.
		 *
		 * @param t The first function argument
		 * @param u The second function argument
		 * @param v The third function argument
		 * @return The function string result
		 */
		CommandMessage apply(T t, U u, V v);
	}
}
