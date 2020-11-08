package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An argument that can parse guilds by their ID for a JDA or the message sent's guild.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class GuildChannelArgument implements Argument<GuildChannel> {
	private static final GuildChannelArgument DEFAULT = new GuildChannelArgument(null);
	@Nullable
	private final JDA jda;

	private GuildChannelArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static GuildChannelArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get channels of the guild that the message was sent from then use {@link #get()}.
	 *
	 * @param jda - JDA to get the guild from.
	 * @return An instance of this argument with a JDA.
	 */
	public static GuildChannelArgument create(JDA jda) {
		return new GuildChannelArgument(jda);
	}

	@Override
	public ParsedArgument<GuildChannel> parse(MessageReader reader) throws CommandSyntaxException {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				GuildChannel foundChannel = this.jda != null ? this.jda.getGuildChannelById(parsedLong) : this.getGuildChannelById(reader.getMessage(), parsedLong);
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
	private GuildChannel getGuildChannelById(Message message, long parsedLong) {
		return message.isFromGuild() ? message.getGuild().getGuildChannelById(parsedLong) : null;
	}
}