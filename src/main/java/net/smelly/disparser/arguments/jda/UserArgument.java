package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An argument that can parse users by their ID or a mention of the user.
 * Define a JDA to get the User from or leave null to use the JDA of the message that was sent.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class UserArgument implements Argument<User> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<@!?(\\d+)>$");
	private static final UserArgument DEFAULT = new UserArgument(null);
	@Nullable
	private final JDA jda;

	private UserArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static UserArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get users of the guild that the message was sent from then use {@link #get()}.
	 *
	 * @param jda - JDA to get the user from.
	 * @return An instance of this argument with a JDA.
	 */
	public static UserArgument create(JDA jda) {
		return new UserArgument(jda);
	}

	@Override
	public ParsedArgument<User> parse(MessageReader reader) throws CommandSyntaxException {
		return reader.parseNextArgument((arg) -> {
			try {
				long id = Long.parseLong(arg);
				User foundUser = this.findUserWithId(reader, id);
				if (foundUser != null) {
					return ParsedArgument.parse(foundUser);
				} else {
					throw reader.getExceptionProvider().getUserNotFoundException().create(id);
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);

				if (matcher.matches()) {
					User foundUser = this.findUserWithId(reader, Long.parseLong(matcher.group(1)));
					if (foundUser != null) {
						return ParsedArgument.parse(foundUser);
					} else {
						throw reader.getExceptionProvider().getMentionUserNotFoundException().create();
					}
				}

				throw reader.getExceptionProvider().getInvalidUserException().create(arg);
			}
		});
	}

	@Nullable
	private User findUserWithId(MessageReader reader, long id) {
		if (this.jda != null) {
			return this.jda.getUserById(id);
		}
		return reader.getMessage().getType() == MessageType.DEFAULT ? reader.getMessage().getJDA().getUserById(id) : null;
	}
}