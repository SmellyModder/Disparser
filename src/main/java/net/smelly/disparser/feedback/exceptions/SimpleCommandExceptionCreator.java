package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A simple class for sending an exception with a message.
 *
 * @author Luke Tonon
 * @see CommandExceptionCreator
 * @see CommandSyntaxException
 */
@ThreadSafe
public final class SimpleCommandExceptionCreator implements CommandExceptionCreator<CommandSyntaxException> {
	private final CommandMessage message;

	public SimpleCommandExceptionCreator(CommandMessage message) {
		this.message = message;
	}

	/**
	 * @return Creates a new {@link CommandSyntaxException} with this creator's message.
	 */
	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.message);
	}
}
