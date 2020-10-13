package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.DisparserExceptions;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument that can parse {@link Role}s by their ID for a JDA or the message sent's {@link Guild}.
 *
 * @author Luke Tonon
 */
public final class RoleArgument implements Argument<Role> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("<@&(\\d+)>");

	@Nullable
	private final JDA jda;

	private RoleArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static RoleArgument get() {
		return new RoleArgument(null);
	}

	/**
	 * If you only want to get {@link Role}s of the {@link Guild} that the message was sent from then use {@link #get()}.
	 *
	 * @param jda - JDA to get the {@link Role} from.
	 * @return An instance of this argument with a JDA.
	 */
	public static RoleArgument create(JDA jda) {
		return new RoleArgument(jda);
	}

	@Override
	public ParsedArgument<Role> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			Guild guild = reader.getChannel().getGuild();
			try {
				long parsedId = Long.parseLong(arg);
				Role foundRole = this.findRole(guild, parsedId);
				if (foundRole != null) {
					return ParsedArgument.parse(foundRole);
				} else {
					throw DisparserExceptions.ROLE_NOT_FOUND_EXCEPTION.create(parsedId);
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);

				if (matcher.matches()) {
					long parsedId = Long.parseLong(matcher.group(1));
					Role foundRole = this.findRole(guild, parsedId);
					if (foundRole != null) {
						return ParsedArgument.parse(foundRole);
					} else {
						throw DisparserExceptions.MENTION_ROLE_NOT_FOUND_EXCEPTION.create();
					}
				}

				throw DisparserExceptions.INVALID_ROLE_ID_EXCEPTION.create(arg);
			}
		});
	}

	@Nullable
	private Role findRole(Guild guild, long id) {
		return this.jda != null ? this.jda.getRoleById(id) : guild.getRoleById(id);
	}
}
