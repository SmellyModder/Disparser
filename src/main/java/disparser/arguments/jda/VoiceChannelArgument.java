package disparser.arguments.jda;

import javax.annotation.Nullable;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.VoiceChannel;

/**
 * @author Luke Tonon
 */
public final class VoiceChannelArgument implements Argument<VoiceChannel> {
	@Nullable
	private final JDA jda;
	
	private VoiceChannelArgument(JDA jda) {
		this.jda = jda;
	}
	
	public static VoiceChannelArgument get() {
		return new VoiceChannelArgument(null);
	}
	
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
					return ParsedArgument.parseError("Voice channel with id " + "`" + arg + "` could not be found");
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`" + arg + "` is not a valid channel id");
			}
		});
	}
}