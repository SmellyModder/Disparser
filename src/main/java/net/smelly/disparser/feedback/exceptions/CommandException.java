package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An exception that represents a command syntax exception.
 *
 * @author Luke Tonon
 * @see SimpleCommandExceptionCreator
 */
@ThreadSafe
@SuppressWarnings("serial")
public class CommandException extends Exception {
	private final CommandMessage message;

	/**
	 * A simple constructor that sets a message for this exception.
	 *
	 * @param message Message for the exception, {@link #getMessage()}.
	 */
	public CommandException(CommandMessage message) {
		this(message, false);
	}

	/**
	 * A simple constructor that sets a message for this exception.
	 *
	 * @param message          Message for the exception, {@link #getMessage()}.
	 * @param enableStacktrace If stack traces should be enabled for this exception.
	 */
	public CommandException(CommandMessage message, boolean enableStacktrace) {
		super(message.getMessage(null), null, true, enableStacktrace);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message.getMessage(null);
	}

	/**
	 * @return The {@link CommandMessage} belonging to this {@link CommandException}.
	 */
	@Nonnull
	public CommandMessage getCommandMessage() {
		return this.message;
	}
}
