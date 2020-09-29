package net.smelly.disparser.arguments.jda;

import net.smelly.disparser.Argument;
import net.smelly.disparser.ArgumentReader;
import net.smelly.disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.smelly.disparser.feedback.DisparserExceptions;

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
	public ParsedArgument<Guild> parse(ArgumentReader reader) throws Exception {
		return reader.parseNextArgument((arg) -> {
			try {
				long id = Long.parseLong(arg);
				Guild guild = this.jda.getGuildById(id);
				if (guild != null) {
					return ParsedArgument.parse(guild);
				} else {
					throw DisparserExceptions.GUILD_NOT_FOUND_EXCEPTION.create(id);
				}
			} catch (NumberFormatException exception) {
				throw DisparserExceptions.INVALID_GUILD_ID_EXCEPTION.create(arg);
			}
		});
	}
}