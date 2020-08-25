package disparser.arguments.jda;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument that can parse text channels by their ID or a mention of the text channel.
 * Define a JDA to get the text channel from or leave null to use the JDA of the message that was sent.
 * 
 * @author Luke Tonon
 */
public final class TextChannelArgument implements Argument<TextChannel> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<#(\\d+)>$");
	
	@Nullable
	private final JDA jda;
	
	private TextChannelArgument(JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static TextChannelArgument get() {
		return new TextChannelArgument(null);
	}
	
	/**
	 * If you only want to get text channels of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the channel from.
	 * @return An instance of this argument with a JDA.
	 */
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
					return ParsedArgument.parseError("Text channel with id `%d` could not be found", parsedLong);
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
				
				return ParsedArgument.parseError("`%s` is not a valid channel id", arg);
			}
		});
	}
}