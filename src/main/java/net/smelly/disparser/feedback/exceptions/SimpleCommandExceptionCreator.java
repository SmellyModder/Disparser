package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple class for sending an exception with a message.
 *
 * @author Luke Tonon
 * @see ExceptionCreator
 * @see CommandException
 */
@ThreadSafe
public final class SimpleCommandExceptionCreator implements ExceptionCreator<CommandException> {
	private final CommandMessage message;

	public SimpleCommandExceptionCreator(CommandMessage message) {
		this.message = message;
	}

	/**
	 * @return Creates a new {@link CommandException} with this creator's message.
	 */
	@Override
	public CommandException create() {
		return new CommandException(this.message);
	}
}
