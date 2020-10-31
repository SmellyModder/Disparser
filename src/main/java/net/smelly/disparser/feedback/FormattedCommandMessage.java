package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implementation class of {@link CommandMessage} where a message is formatted with {@link String#format(String, Object...)}.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public class FormattedCommandMessage implements CommandMessage {
	private String formattedMessage;

	public FormattedCommandMessage(String message, Object... args) {
		this.formattedMessage = String.format(message, args);
	}

	@Override
	public String getMessage(@Nullable MessageChannel channel) {
		return this.formattedMessage;
	}
}
