package disparser.arguments.jda;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * An argument that can parse guilds by their ID for a JDA.
 * 
 * @author Luke Tonon
 */
public final class GuildArgument implements Argument<Guild> {
	private final JDA jda;
	
	private GuildArgument(JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @param jda - The JDA to get the guild from.
	 * @return An instance of this argument with a JDA.
	 */
	public static GuildArgument get(JDA jda) {
		return new GuildArgument(jda);
	}
	
	@Override
	public ParsedArgument<Guild> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				Guild guild = this.jda.getGuildById(Long.parseLong(arg));
				if (guild != null) {
					return ParsedArgument.parse(guild);
				} else {
					return ParsedArgument.parseError("Guild with id `%s` could not be found");
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`%s` is not a valid guild id", arg);
			}
		});
	}
}