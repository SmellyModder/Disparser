package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.concurrent.ThreadSafe;

/**
 * An exception that represents a command syntax exception.
 *
 * @author Luke Tonon
 * @see SimpleCommandExceptionCreator
 */
@ThreadSafe
@SuppressWarnings("serial")
public class CommandSyntaxException extends Exception {
	private final CommandMessage message;

	/**
	 * A simple constructor that sets a message for this exception.
	 *
	 * @param message Message for the exception, {@link #getMessage()}.
	 */
	public CommandSyntaxException(CommandMessage message) {
		super(message.getMessage(null), null, true, false);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message.getMessage(null);
	}

	/**
	 * @return The {@link CommandMessage} belonging to this {@link CommandSyntaxException}.
	 */
	public CommandMessage getCommandMessage() {
		return this.message;
	}
}
