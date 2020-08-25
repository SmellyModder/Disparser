package disparser.arguments.jda;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nullable;

/**
 * An argument that can parse voice channels by their ID.
 * Define a JDA to get the voice channel from or leave null to use the JDA of the message that was sent.
 * 
 * @author Luke Tonon
 */
public final class VoiceChannelArgument implements Argument<VoiceChannel> {
	@Nullable
	private final JDA jda;
	
	private VoiceChannelArgument(JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static VoiceChannelArgument get() {
		return new VoiceChannelArgument(null);
	}
	
	/**
	 * If you only want to get voice channels of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the channel from.
	 * @return An instance of this argument with a JDA.
	 */
	public static VoiceChannelArgument create(JDA jda) {
		return new VoiceChannelArgument(jda);
	}
	
	@Override
	public ParsedArgument<VoiceChannel> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				VoiceChannel foundChannel = this.jda != null ? this.jda.getVoiceChannelById(parsedLong) : reader.getChannel().getGuild().getVoiceChannelById(parsedLong);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					return ParsedArgument.parseError("Voice channel with id `%d` could not be found", parsedLong);
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`%s` is not a valid channel id", arg);
			}
		});
	}
}