package disparser.arguments.jda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

/**
 * @author Luke Tonon
 */
public final class UserArgument implements Argument<User> {
	private static final Pattern MENTION_PATTERN = Pattern.compile("^<@!?(\\d+)>$");
	
	@Nullable
	private final JDA jda;
	
	private UserArgument(JDA jda) {
		this.jda = jda;
	}
	
	public static UserArgument get() {
		return new UserArgument(null);
	}
	
	public static UserArgument create(JDA jda) {
		return new UserArgument(jda);
	}
	
	@Override
	public ParsedArgument<User> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				User foundUser = this.findUserWithId(reader, Long.parseLong(arg));
				if (foundUser != null) {
					return ParsedArgument.parse(foundUser);
				} else {
					return ParsedArgument.parseError("Member with id " + "`" + arg + "` could not be found");
				}
			} catch (NumberFormatException exception) {
				Matcher matcher = MENTION_PATTERN.matcher(arg);
				
				if (matcher.matches()) {
					User foundUser = this.findUserWithId(reader, Long.parseLong(matcher.group(1)));
					if (foundUser != null) {
						return ParsedArgument.parse(foundUser);
					} else {
						return ParsedArgument.parseError("Member in mention could not be found");
					}
				}
				
				return ParsedArgument.parseError("`" + arg + "` is not a valid member id or valid user mention");
			}
		});
	}
	
	@Nullable
	private User findUserWithId(ArgumentReader reader, long id) {
		return this.jda != null ? this.jda.getUserById(id) : reader.getChannel().getJDA().getUserById(id);
	}
}