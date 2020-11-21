package net.smelly.disparser.context;

import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;

/**
 * A consumer-like functional interface that can throw {@link CommandException}s in its accept method.
 *
 * @param <C> The type of {@link CommandContext} to accept.
 * @author Luke Tonon
 */
public interface ContextConsumer<C extends CommandContext<?>> {
	/**
	 * Accepts a {@link CommandContext} and performs an operation.
	 *
	 * @param context A {@link CommandContext} to accept.
	 * @throws CommandException If a command error occurs trying to accept the {@link CommandContext}.
	 */
	void accept(@Nonnull C context) throws CommandException;
}
