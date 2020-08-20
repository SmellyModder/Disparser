package disparser.arguments.jda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * @author Luke Tonon
 */
public final class TextChannelArgument implements Argument<TextChannel> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<#(\\d+)>$");
	
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
					return ParsedArgument.parseError("Text channel with id " + "`" + arg + "` could not be found");
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);
				
				if (matcher.matches()) {
					TextChannel channel = this.jda.getTextChannelById(Long.parseLong(matcher.group(1)));
					if (channel != null) {
						return ParsedArgument.parse(channel);
					} else {
						return ParsedArgument.parseError("Text Channel in mention could not be found");
					}
				}
				
				return ParsedArgument.parseError("`" + arg + "` is not a valid channel id");
			}
		});
	}
}