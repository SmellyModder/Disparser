package disparser.arguments.jda;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;

/**
 * An argument that can parse webhooks by their ID.
 * Define a JDA to get the webhook from or leave null to use the JDA of the message that was sent.
 * 
 * @author Luke Tonon
 */
public final class WebhookArgument implements Argument<Webhook> {
	@Nullable
	private final JDA jda;
	
	private WebhookArgument(JDA jda) {
		this.jda = jda;
	}
	
	/**
	 * @return A default instance.
	 */
	public static WebhookArgument get() {
		return new WebhookArgument(null);
	}
	
	/**
	 * If you only want to get webhooks of the guild that the message was sent from then use {@link #get()}.
	 * @param jda - JDA to get the webhook from.
	 * @return An instance of this argument with a JDA.
	 */
	public static WebhookArgument create(JDA jda) {
		return new WebhookArgument(jda);
	}
	
	@Override
	public ParsedArgument<Webhook> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				Webhook foundWebhook;
				try {
					foundWebhook = this.jda == null ? reader.getChannel().getJDA().retrieveWebhookById(parsedLong).submit().get() : this.jda.retrieveWebhookById(parsedLong).submit().get();
					if (foundWebhook != null) {
						return ParsedArgument.parse(foundWebhook);
					} else {
						return ParsedArgument.parseError("Text channel with id `%d` could not be found", parsedLong);
					}
				} catch (InterruptedException | ExecutionException e) {
					return ParsedArgument.parseError("An exception occured when trying to process the webhook with id `%d`", parsedLong);
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`%s` is not a valid channel id", arg);
			}
		});
	}
}