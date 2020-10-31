package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This interface is used to build {@link FeedbackHandler}s.
 * <p>Implement this on types that will be used to build a {@link FeedbackHandler}.</p>
 *
 * @author Luke Tonon
 * @see FeedbackHandler
 */
@ThreadSafe
@FunctionalInterface
public interface FeedbackHandlerBuilder {
	FeedbackHandlerBuilder SIMPLE_BUILDER = SimpleFeedbackHandler::new;

	/**
	 * Builds a {@link FeedbackHandler}.
	 *
	 * @param channel The {@link MessageChannel} for building this {@link FeedbackHandler}.
	 * @return a built {@link FeedbackHandler}.
	 */
	FeedbackHandler build(MessageChannel channel);
}
