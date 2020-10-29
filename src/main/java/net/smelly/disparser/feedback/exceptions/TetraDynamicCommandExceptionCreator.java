package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nullable;

/**
 * This class contains a {@link TetraDynamicCommandExceptionCreator.TetraFunction} for creating a {@link CommandSyntaxException}.
 * The function takes in four generic type objects and uses them to create a {@link CommandMessage} to be used for creating a {@link CommandSyntaxException}.
 * This class can store the four generic objects internally to be re-used for creating a {@link CommandSyntaxException}.
 * <p> Simply put, this class is a quadruple version of {@link DynamicCommandExceptionCreator}. </p>
 *
 * @author Luke Tonon
 * @see DynamicCommandExceptionCreator
 */
public class TetraDynamicCommandExceptionCreator<T, U, V, W> implements CommandExceptionCreator<CommandSyntaxException> {
	private final TetraFunction<T, U, V, W> function;
	@Nullable
	private final T first;
	@Nullable
	private final U second;
	@Nullable
	private final V third;
	@Nullable
	private final W fourth;

	public TetraDynamicCommandExceptionCreator(TetraFunction<T, U, V, W> function) {
		this(null, null, null, null, function);
	}

	public TetraDynamicCommandExceptionCreator(@Nullable T first, @Nullable U second, @Nullable V third, @Nullable W fourth, TetraFunction<T, U, V, W> function) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.function = function;
	}

	public static <T, U, V, W> TetraDynamicCommandExceptionCreator<T, U, V, W> createInstance(TetraFunction<T, U, V, W> function) {
		return new TetraDynamicCommandExceptionCreator<>(function);
	}

	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.function.apply(this.first, this.second, this.third, this.fourth));
	}

	public CommandSyntaxException create(T first, U second, V third, W fourth) {
		return new CommandSyntaxException(this.function.apply(first, second, third, fourth));
	}

	public interface TetraFunction<T, U, V, W> {
		/**
		 * Applies this function to the given arguments.
		 *
		 * @param t The first function argument.
		 * @param u The second function argument.
		 * @param v The third function argument.
		 * @param w The third function argument.
		 * @return The function {@link CommandMessage} result.
		 */
		CommandMessage apply(T t, U u, V v, W w);
	}
}
