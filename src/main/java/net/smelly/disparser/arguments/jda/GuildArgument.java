package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * An argument that can parse guilds by their ID for a JDA.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class GuildArgument implements Argument<Guild> {
	private final JDA jda;

	private GuildArgument(JDA jda) {
		this.jda = jda;
	}

	/**
	 * @param jda A {@link JDA} to get the guild from.
	 * @return An instance of this argument with a JDA.
	 */
	public static GuildArgument get(JDA jda) {
		return new GuildArgument(jda);
	}

	@Nonnull
	@Override
	public ParsedArgument<Guild> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			try {
				long id = Long.parseLong(arg);
				Guild guild = this.jda.getGuildById(id);
				if (guild != null) {
					return ParsedArgument.parse(guild);
				} else {
					throw reader.getExceptionProvider().getGuildNotFoundException().create(id);
				}
			} catch (NumberFormatException exception) {
				throw reader.getExceptionProvider().getInvalidGuildIdException().create(arg);
			}
		});
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		GuildArgument that = (GuildArgument) o;
		return Objects.equals(this.jda, that.jda);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.jda);
	}

	@Override
	public String toString() {
		return "GuildArgument{" +
				"jda=" + this.jda +
				'}';
	}
}