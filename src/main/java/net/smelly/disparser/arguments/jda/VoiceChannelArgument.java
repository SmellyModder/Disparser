package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * An argument that can parse voice channels by their ID.
 * Define a JDA to get the voice channel from or leave null to use the JDA of the message that was sent.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class VoiceChannelArgument implements Argument<VoiceChannel> {
	private static final VoiceChannelArgument DEFAULT = new VoiceChannelArgument(null);
	@Nullable
	private final JDA jda;

	private VoiceChannelArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static VoiceChannelArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get voice channels of the guild that the message was sent from then use {@link #get()}.
	 *
	 * @param jda A {@link JDA} to get the channel from.
	 * @return An instance of this argument with a JDA.
	 */
	public static VoiceChannelArgument create(JDA jda) {
		return new VoiceChannelArgument(jda);
	}

	@Nonnull
	@Override
	public ParsedArgument<VoiceChannel> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				VoiceChannel foundChannel = this.jda != null ? this.jda.getVoiceChannelById(parsedLong) : this.getVoiceChannelById(reader.getMessage(), parsedLong);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					throw reader.getExceptionProvider().getChannelNotFoundException().create(parsedLong);
				}
			} catch (NumberFormatException exception) {
				throw reader.getExceptionProvider().getInvalidChannelIdException().create(arg);
			}
		});
	}

	@Nullable
	private VoiceChannel getVoiceChannelById(Message message, long parsedLong) {
		return message.isFromGuild() ? message.getGuild().getVoiceChannelById(parsedLong) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		VoiceChannelArgument that = (VoiceChannelArgument) o;
		return Objects.equals(this.jda, that.jda);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.jda);
	}

	@Override
	public String toString() {
		return "VoiceChannelArgument{" +
				"jda=" + (this.jda != null ? this.jda : "undefined") +
				'}';
	}
}