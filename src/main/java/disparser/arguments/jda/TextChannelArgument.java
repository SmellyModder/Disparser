package disparser.arguments.jda;

import javax.annotation.Nullable;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class TextChannelArgument implements Argument<TextChannel> {
	@Nullable
	private final JDA jda;
	
	private TextChannelArgument(JDA jda) {
		this.jda = jda;
	}
	
	public static TextChannelArgument get() {
		return new TextChannelArgument(null);
	}
	
	public static TextChannelArgument create(JDA jda) {
		return new TextChannelArgument(jda);
	}
	
	@Override
	public ParsedArgument<TextChannel> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				TextChannel foundChannel = this.jda != null ? this.jda.getTextChannelById(parsedLong) : reader.getChannel().getGuild().getTextChannelById(parsedLong);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					return ParsedArgument.parseError("Text channel with id " + "`" + arg + "`" + " could not be found");
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`" + arg + "`" + " is not a valid channel id");
			}
		});
	}
}