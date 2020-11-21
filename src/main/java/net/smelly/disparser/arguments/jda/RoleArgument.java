package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
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
 * An argument that can parse {@link Role}s by their ID for a JDA or the message sent's {@link Guild}.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class RoleArgument implements Argument<Role> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("<@&(\\d+)>");
	private static final RoleArgument DEFAULT = new RoleArgument(null);
	@Nullable
	private final JDA jda;

	private RoleArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static RoleArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get {@link Role}s of the {@link Guild} that the message was sent from then use {@link #get()}.
	 *
	 * @param jda A {@link JDA} to get the {@link Role} from.
	 * @return An instance of this argument with a JDA.
	 */
	public static RoleArgument create(JDA jda) {
		return new RoleArgument(jda);
	}

	@Nonnull
	@Override
	public ParsedArgument<Role> parse(MessageReader reader) throws CommandException {
		return reader.parseNextArgument((arg) -> {
			Guild guild = reader.getGuild();
			try {
				long parsedId = Long.parseLong(arg);
				Role foundRole = this.findRole(guild, parsedId);
				if (foundRole != null) {
					return ParsedArgument.parse(foundRole);
				} else {
					throw reader.getExceptionProvider().getRoleNotFoundException().create(parsedId);
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);

				if (matcher.matches()) {
					long parsedId = Long.parseLong(matcher.group(1));
					Role foundRole = this.findRole(guild, parsedId);
					if (foundRole != null) {
						return ParsedArgument.parse(foundRole);
					} else {
						throw reader.getExceptionProvider().getMentionRoleNotFoundException().create();
					}
				}

				throw reader.getExceptionProvider().getInvalidRoleIdException().create(arg);
			}
		});
	}

	@Nullable
	private Role findRole(@Nullable Guild guild, long id) {
		return this.jda != null ? this.jda.getRoleById(id) : guild != null ? guild.getRoleById(id) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		RoleArgument that = (RoleArgument) o;
		return Objects.equals(this.jda, that.jda);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.jda);
	}

	@Override
	public String toString() {
		return "RoleArgument{" +
				"jda=" + (this.jda != null ? this.jda : "undefined") +
				'}';
	}
}
