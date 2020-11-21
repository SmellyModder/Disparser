package net.smelly.disparser.context;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Predicate;

/**
 * A functional interface that returns a {@link Result} describing the result of testing an {@link Event}. This allows for localized messages as to why a requirement was not met.
 *
 * @param <E> The type of {@link Event} to test this requirement for.
 * @author Luke Tonon
 */
@ThreadSafe
@FunctionalInterface
public interface Requirement<E extends Event> {
	/**
	 * Creates a new {@link Requirement} that always returns {@link Result#PASSED} as its result.
	 *
	 * @param <E> The type of {@link Event} to test this requirement for.
	 * @return A new {@link Requirement} that always returns {@link Result#PASSED} as its result.
	 */
	static <E extends Event> Requirement<E> none() {
		return event -> Result.PASSED;
	}

	/**
	 * Creates a new {@link Requirement} that returns {@link Result#PASSED} for a given {@link Predicate} and creates a {@link Result#failed(CommandMessage)} if the test failed.
	 *
	 * @param predicate The {@link Predicate} to test on the {@link Event}
	 * @param reason    A {@link CommandMessage} describing why the test failed.
	 * @param <E>       The type of {@link Event} to test this requirement for.
	 * @return A new {@link Requirement} that always returns {@link Result#PASSED} as its result.
	 */
	static <E extends Event> Requirement<E> test(Predicate<E> predicate, CommandMessage reason) {
		return event -> predicate.test(event) ? Result.PASSED : Result.failed(reason);
	}

	/**
	 * Tests an {@link Event} and returns a {@link Result} for the test.
	 *
	 * @param event An {@link Event} to test.
	 * @return A {@link Result} from the test.
	 */
	Result test(E event);

	/**
	 * An enum representing the two possible results for a test.
	 *
	 * @author Luke Tonon
	 */
	enum ResultType {
		PASSED,
		FAILED
	}

	/**
	 * A container class containing a {@link ResultType} and {@link CommandMessage} for describing the result of a test.
	 * <p>Instances of this class should be cached when possible.</p>
	 *
	 * @author Luke Tonon
	 */
	@Immutable
	class Result {
		public static final Result PASSED = new Result(ResultType.PASSED, CommandMessage.EMPTY);
		private final ResultType type;
		private final CommandMessage reason;

		private Result(ResultType type, CommandMessage reason) {
			this.type = type;
			this.reason = reason;
		}

		/**
		 * Creates a new {@link ResultType#FAILED} {@link Result} with a given {@link CommandMessage} reason.
		 *
		 * @param reason A {@link CommandMessage} describing why the test failed. This can be empty, but of course should try not to be as that'd be very undescriptive to a user.
		 * @return A new {@link ResultType#FAILED} {@link Result} with a given {@link CommandMessage} reason.
		 */
		public static Result failed(@Nonnull CommandMessage reason) {
			return new Result(ResultType.FAILED, reason);
		}

		/**
		 * Gets the {@link CommandMessage} reason for this result.
		 *
		 * @return The {@link CommandMessage} reason for this result.
		 */
		public CommandMessage getReason() {
			return this.reason;
		}

		/**
		 * Gets the {@link ResultType} for this result.
		 * <p>This can be assumed to return {@link ResultType#FAILED} if the {@link #reason} is not empty</p>
		 *
		 * @return The {@link ResultType} for this result.
		 */
		public ResultType getType() {
			return this.type;
		}
	}
}
