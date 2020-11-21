package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.properties.CommandPropertyMap;

import java.util.Map;

/**
 * An extension of {@link CommandContext} for the {@link PrivateMessageReceivedEvent} event.
 *
 * @author Luke Tonon
 * @see CommandContext
 */
public final class PrivateMessageCommandContext extends CommandContext<PrivateMessageReceivedEvent> {

	public PrivateMessageCommandContext(PrivateMessageReceivedEvent event, Map<Integer, ParsedArgument<?>> parsedArguments, CommandPropertyMap.PropertyMap propertyMap, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider) {
		super(event, parsedArguments, propertyMap, feedbackHandler, exceptionProvider);
	}

	public static class Builder extends CommandContextBuilder<PrivateMessageReceivedEvent, PrivateMessageCommandContext> {

		public Builder(PrivateMessageReceivedEvent event, CommandPropertyMap.PropertyMap propertyMap, MessageChannel channel, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider, MessageReader reader) {
			super(event, propertyMap, channel, feedbackHandler, exceptionProvider, reader);
		}

		@Override
		public PrivateMessageCommandContext build() {
			return new PrivateMessageCommandContext(this.getEvent(), this.getArguments(), this.getPropertyMap(), this.getFeedbackHandler(), this.getExceptionProvider());
		}

	}

}
