package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument that can parse text channels by their ID or a mention of the text channel.
 * Define a JDA to get the text channel from or leave null to use the JDA of the message that was sent.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class TextChannelArgument implements Argument<TextChannel> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<#(\\d+)>$");
	private static final TextChannelArgument DEFAULT = new TextChannelArgument(null);
	@Nullable
	private final JDA jda;

	private TextChannelArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static TextChannelArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get text channels of the guild that the message was sent from then use {@link #get()}.
	 *
	 * @param jda A {@link JDA} to get the channel from.
	 * @return An instance of this argument with a JDA.
	 */
	public static TextChannelArgument create(JDA jda) {
		return new TextChannelArgument(jda);
	}

	@Nonnull
	@Override
	public ParsedArgument<TextChannel> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			Guild guild = reader.getGuild();
			try {
				long parsedId = Long.parseLong(arg);
				TextChannel foundChannel = this.findChannelWithId(guild, parsedId);
				if (foundChannel != null) {
					return ParsedArgument.parse(foundChannel);
				} else {
					throw reader.getExceptionProvider().getChannelNotFoundException().create(parsedId);
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);

				if (matcher.matches()) {
					long parsedId = Long.parseLong(matcher.group(1));
					TextChannel foundChannel = this.findChannelWithId(guild, parsedId);
					if (foundChannel != null) {
						return ParsedArgument.parse(foundChannel);
					} else {
						throw reader.getExceptionProvider().getMentionChannelNotFoundException().create();
					}
				}

				throw reader.getExceptionProvider().getInvalidChannelIdException().create(arg);
			}
		});
	}

	@Nullable
	private TextChannel findChannelWithId(@Nullable Guild guild, long id) {
		return this.jda != null ? this.jda.getTextChannelById(id) : guild != null ? guild.getTextChannelById(id) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		TextChannelArgument that = (TextChannelArgument) o;
		return Objects.equals(this.jda, that.jda);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.jda);
	}

	@Override
	public String toString() {
		return "TextChannelArgument{" +
				"jda=" + (this.jda != null ? this.jda : "undefined") +
				'}';
	}
}