package disparser.feedback;

import disparser.CommandContext;

/**
 * This interface is used to build {@link FeedbackHandler}s.
 * Implement this on types that will be used to build a {@link FeedbackHandler}.
 * @see FeedbackHandler
 * @author Luke Tonon
 */
public interface FeedbackHandlerBuilder {
	FeedbackHandlerBuilder SIMPLE_BUILDER = SimpleFeedbackHandler::new;

	/**
	 * Builds a {@link FeedbackHandler}.
	 * @param commandContext The {@link CommandContext} for building this {@link FeedbackHandler}.
	 * @return a built {@link FeedbackHandler}.
	 */
	FeedbackHandler build(CommandContext commandContext);
}
