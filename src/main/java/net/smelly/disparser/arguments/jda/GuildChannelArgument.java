package net.smelly.disparser.arguments.jda;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.DisparserExceptions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;

import javax.annotation.Nullable;

/**
 * An argument that can parse guilds by their ID for a JDA or the message sent's guild.
 * 
 * @author Luke Tonon
 */
public final class GuildChannelArgument implements Argument<GuildChannel> {
	@Nullable
	private final JDA jda;
	
	private GuildChannelArgument(@Nullable JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static GuildChannelArgument get() {
		return new GuildChannelArgument(null);
	}
	
	/**
	 * If you only want to get channels of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the guild from.
	 * @return An instance of this argument with a JDA.
	 */
	public static GuildChannelArgument create(JDA jda) {
		return new GuildChannelArgument(jda);
	}
	
	@Override
	public ParsedArgument<GuildChannel> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				GuildChannel foundChannel = this.jda != null ? this.jda.getGuildChannelById(parsedLong) : reader.getChannel().getGuild().getGuildChannelById(parsedLong);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					throw DisparserExceptions.CHANNEL_NOT_FOUND_EXCEPTION.create(parsedLong);
				}
			} catch (NumberFormatException exception) {
				throw DisparserExceptions.INVALID_CHANNEL_ID_EXCEPTION.create(arg);
			}
		});
	}
}