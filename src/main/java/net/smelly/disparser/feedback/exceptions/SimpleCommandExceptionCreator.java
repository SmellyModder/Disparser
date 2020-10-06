package net.smelly.disparser.feedback.exceptions;

import net.smelly.disparser.feedback.exceptions.CommandExceptionCreator;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

/**
 * A simple class for sending an exception with a message.
 *
 * @author Luke Tonon
 * @see CommandExceptionCreator
 * @see CommandSyntaxException
 */
public final class SimpleCommandExceptionCreator implements CommandExceptionCreator<CommandSyntaxException> {
	private final String message;

	public SimpleCommandExceptionCreator(String message) {
		this.message = message;
	}

	/**
	 * @return Creates a new {@link CommandSyntaxException} with this creator's message.
	 */
	@Override
	public CommandSyntaxException create() {
		return new CommandSyntaxException(this.message);
	}

	/**
	 * @return Creates a new {@link CommandSyntaxException} with this creator's message.
	 */
	public CommandSyntaxException createForArgument(int argument) {
		return new CommandSyntaxException(this.message, argument);
	}
}
