package disparser.arguments.jda;

import java.util.concurrent.ExecutionException;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Webhook;

public class WebhookArgument implements Argument<Webhook> {
	private final JDA jda;
	
	private WebhookArgument(JDA jda) {
		this.jda = jda;
	}
	
	public static WebhookArgument get(JDA jda) {
		return new WebhookArgument(jda);
	}
	
	@Override
	public ParsedArgument<Webhook> parse(ArgumentReader reader) {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				Webhook foundWebhook;
				try {
					foundWebhook = this.jda.retrieveWebhookById(parsedLong).submit().get();
					if (foundWebhook != null) {
						return ParsedArgument.parse(foundWebhook);
					} else {
						return ParsedArgument.parseError("Text channel with id `" + arg + "` could not be found");
					}
				} catch (InterruptedException | ExecutionException e) {
					return ParsedArgument.parseError("An exception occured when trying to process the webhook with id `" + arg + "`");
				}
			} catch (NumberFormatException exception) {
				return ParsedArgument.parseError("`" + arg + "` is not a valid channel id");
			}
		});
	}
}