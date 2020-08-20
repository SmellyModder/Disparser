package disparser.arguments.jda;

import javax.annotation.Nullable;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildChannel;

/**
 * @author Luke Tonon
 */
public final class GuildChannelArgument implements Argument<GuildChannel> {
	@Nullable
	private final JDA jda;
	
	private GuildChannelArgument(JDA jda) {
		this.jda = jda;
	}
	
	public static GuildChannelArgument get() {
		return new GuildChannelArgument(null);
	}
	
	public static GuildChannelArgument create(JDA jda) {
		return new GuildChannelArgument(jda);
	}
	
	@Override
	public ParsedArgument<GuildChannel> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				GuildChannel foundChannel = this.jda != null ? this.jda.getGuildChannelById(parsedLong) : reader.getChannel().getGuild().getGuildChannelById(parsedLong);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					return ParsedArgument.parseError("Channel with id " + "`" + arg + "` could not be found");
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`" + arg + "` is not a valid channel id");
			}
		});
	}
}