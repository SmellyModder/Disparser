package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * An implementation of {@link CommandMessage} where a message is formatted with {@link String#format(String, Object...)}.
 *
 * @author Luke Tonon
 */
@Immutable
public final class FormattedCommandMessage implements CommandMessage {
	private final String formattedMessage;

	public FormattedCommandMessage(String message, Object... args) {
		this.formattedMessage = String.format(message, args);
	}

	@Nonnull
	@Override
	public String getMessage(@Nullable MessageChannel channel) {
		return this.formattedMessage;
	}
}
