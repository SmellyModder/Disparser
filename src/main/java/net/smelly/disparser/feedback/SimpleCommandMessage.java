package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * An implementation of {@link CommandMessage} for holding a constant string value.
 *
 * @author Luke Tonon
 */
@Immutable
public final class SimpleCommandMessage implements CommandMessage {
	private final String message;

	public SimpleCommandMessage(String message) {
		this.message = message;
	}

	@Nonnull
	@Override
	public String getMessage(@Nullable MessageChannel channel) {
		return this.message;
	}

	@Override
	public String toString() {
		return "SimpleCommandMessage{" +
				"message='" + this.message +
				'}';
	}
}
