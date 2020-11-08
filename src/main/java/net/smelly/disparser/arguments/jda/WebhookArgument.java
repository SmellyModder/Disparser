package net.smelly.disparser.arguments.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Webhook;
import net.smelly.disparser.Argument;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.exceptions.CommandSyntaxException;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ExecutionException;

/**
 * An argument that can parse {@link Webhook}s by their ID.
 * Define a {@link JDA} to get a {@link Webhook} from or leave null to use the {@link JDA} of the message that was sent.
 *
 * @author Luke Tonon
 */
@ThreadSafe
public final class WebhookArgument implements Argument<Webhook> {
	private static final WebhookArgument DEFAULT = new WebhookArgument(null);
	@Nullable
	private final JDA jda;

	private WebhookArgument(@Nullable JDA jda) {
		this.jda = jda;
	}

	/**
	 * @return A default instance.
	 */
	public static WebhookArgument get() {
		return DEFAULT;
	}

	/**
	 * If you only want to get webhooks of the guild that the message was sent from then use {@link #get()}.
	 *
	 * @param jda A {@link JDA} to get the {@link Webhook} from.
	 * @return An instance of this argument with a JDA.
	 */
	public static WebhookArgument create(JDA jda) {
		return new WebhookArgument(jda);
	}

	@Override
	public ParsedArgument<Webhook> parse(MessageReader reader) throws CommandSyntaxException {
		return reader.parseNextArgument((arg) -> {
			try {
				long parsedLong = Long.parseLong(arg);
				try {
					Webhook foundWebhook = this.jda == null ? this.getWebhookById(reader.getMessage(), parsedLong) : this.jda.retrieveWebhookById(parsedLong).submit().get();
					if (foundWebhook != null) {
						return ParsedArgument.parse(foundWebhook);
					} else {
						throw reader.getExceptionProvider().getWebhookNotFoundException().create(parsedLong);
					}
				} catch (ExecutionException | InterruptedException exception) {
					throw reader.getExceptionProvider().getWebhookNotFoundException().create(parsedLong);
				}
			} catch (NumberFormatException exception) {
				throw reader.getExceptionProvider().getInvalidWebhookIdException().create(arg);
			}
		});
	}

	@Nullable
	private Webhook getWebhookById(Message message, long parsedLong) throws ExecutionException, InterruptedException {
		return message.getType() == MessageType.DEFAULT ? message.getJDA().retrieveWebhookById(parsedLong).submit().get() : null;
	}
}