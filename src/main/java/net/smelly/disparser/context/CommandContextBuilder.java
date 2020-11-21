package net.smelly.disparser.context;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.ParsedArgument;
import net.smelly.disparser.context.tree.DisparsingNode;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.FeedbackHandler;
import net.smelly.disparser.feedback.exceptions.BuiltInExceptionProvider;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.properties.CommandPropertyMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An abstract builder class for {@link CommandContext}s.
 * <p>This is used internally for disparsing nodes to a builder to create a {@link CommandContext}.</p>
 *
 * @param <E> The type of {@link Event} for this builder.
 * @param <C> The type of {@link CommandContext} for this builder.
 * @author Luke Tonon
 */
@NotThreadSafe
public abstract class CommandContextBuilder<E extends Event, C extends CommandContext<E>> {
	private final Map<Integer, ParsedArgument<?>> arguments = new HashMap<>();
	private final CommandPropertyMap.PropertyMap propertyMap;
	private final E event;
	private final MessageChannel channel;
	private final FeedbackHandler feedbackHandler;
	private final BuiltInExceptionProvider exceptionProvider;
	private final MessageReader reader;
	@Nullable
	private ContextConsumer<C> consumer;
	@Nullable
	private Exception exception;
	private int nextArg;

	public CommandContextBuilder(E event, CommandPropertyMap.PropertyMap propertyMap, MessageChannel channel, FeedbackHandler feedbackHandler, BuiltInExceptionProvider exceptionProvider, MessageReader reader) {
		this.event = event;
		this.propertyMap = propertyMap;
		this.channel = channel;
		this.feedbackHandler = feedbackHandler;
		this.exceptionProvider = exceptionProvider;
		this.reader = reader;
	}

	/**
	 * Disparses a {@link RootNode} for a given {@link CommandContextBuilder}.
	 *
	 * @param builder  A {@link CommandContextBuilder} to disparse for.
	 * @param rootNode A {@link RootNode} to disparse.
	 * @param <E>      The type of {@link Event} to disparse for.
	 * @param <C>      The type of {@link CommandContext} for the {@link CommandContextBuilder}.
	 * @return A {@link CommandContextBuilder} after being disparsed by a {@link RootNode}.
	 * @see #disparse(CommandContextBuilder, DisparsingNode)
	 */
	public static <E extends Event, C extends CommandContext<E>> CommandContextBuilder<E, C> disparseRoot(CommandContextBuilder<E, C> builder, RootNode<E, C> rootNode) {
		Requirement.Result result = rootNode.getRequirement().test(builder.event);
		if (result.getType() != Requirement.ResultType.FAILED) {
			try {
				rootNode.disparse(builder, builder.reader);
			} catch (CommandException e) {
				return builder.setException(e);
			}
			return disparse(builder, rootNode);
		}
		return builder.setException(builder.exceptionProvider.getRequirementFailedException().create(result.getReason().getMessage(builder.channel)));
	}

	/**
	 * This method is the core algorithm used in disparsing nodes.
	 * <p>It uses recursion and iteration through nodes to parse them onto a {@link CommandContextBuilder}, similarly to a decision tree.</p>
	 * <p>This also handles node requirements.</p>
	 *
	 * @param builder A {@link CommandContextBuilder} to disparse for.
	 * @param node    A {@link DisparsingNode} to disparse.
	 * @param <E>     The type of {@link Event} to disparse for.
	 * @param <C>     The type of {@link CommandContext} for the {@link CommandContextBuilder}.
	 * @return A {@link CommandContextBuilder} that's ran through disparsing the node.
	 */
	public static <E extends Event, C extends CommandContext<E>> CommandContextBuilder<E, C> disparse(CommandContextBuilder<E, C> builder, DisparsingNode<E, C> node) {
		MessageReader reader = builder.reader;
		Iterator<DisparsingNode<E, C>> children = node.getChildren().iterator();
		while (children.hasNext()) {
			int prevIndex = reader.getIndex();
			DisparsingNode<E, C> child = children.next();
			try {
				Requirement.Result result = child.getRequirement().test(builder.event);
				if (result.getType() != Requirement.ResultType.FAILED) {
					child.disparse(builder, reader);
					if (!child.getChildren().isEmpty()) {
						if (!reader.hasNextArg() && builder.getConsumer() == null) {
							return builder.setException(builder.exceptionProvider.getIncompleteCommandException().create(builder.channel, child.getChildren(), reader.getIndex()));
						}
						return disparse(builder, child);
					} else {
						return builder.setConsumer(child.getConsumer());
					}
				} else if (!children.hasNext()) {
					return builder.setException(builder.exceptionProvider.getRequirementFailedException().create(result.getReason().getMessage(builder.channel)));
				}
			} catch (CommandException syntaxException) {
				if (reader.getIndex() > prevIndex) {
					try {
						reader.lastArgument();
					} catch (Exception exception) {//Shouldn't happen, but we'll catch anyway
						return builder.setException(exception);
					}
					if (reader.hasNextArg() && !children.hasNext()) {
						return builder.setException(builder.exceptionProvider.getArgumentErrorException().create(child.getName(builder.channel), syntaxException.getCommandMessage().getMessage(builder.channel), reader.getIndex() + 1));
					}
				}
			} catch (Exception exception) {
				return builder.setException(builder.exceptionProvider.getUnexpectedErrorException().create(exception.getMessage()));
			}
		}
		return builder;
	}

	/**
	 * Gets this builder's {@link #event}.
	 *
	 * @return This builder's {@link #event}.
	 */
	public E getEvent() {
		return this.event;
	}

	/**
	 * Gets this builder's {@link #propertyMap}.
	 *
	 * @return This builder's {@link #propertyMap}.
	 */
	public CommandPropertyMap.PropertyMap getPropertyMap() {
		return this.propertyMap;
	}

	/**
	 * Gets this builder's {@link #channel}.
	 *
	 * @return This builder's {@link #channel}.
	 */
	public MessageChannel getChannel() {
		return this.channel;
	}

	/**
	 * Gets this builder's {@link #feedbackHandler}.
	 *
	 * @return This builder's {@link #feedbackHandler}.
	 */
	public FeedbackHandler getFeedbackHandler() {
		return this.feedbackHandler;
	}

	/**
	 * Gets this builder's {@link #consumer}.
	 *
	 * @return This builder's {@link #consumer}.
	 */
	@Nullable
	public ContextConsumer<C> getConsumer() {
		return this.consumer;
	}

	/**
	 * Sets this builder's {@link #consumer}.
	 *
	 * @param consumer A {@link ContextConsumer} to set.
	 * @return This builder.
	 */
	public CommandContextBuilder<E, C> setConsumer(@Nullable ContextConsumer<C> consumer) {
		this.consumer = consumer;
		return this;
	}

	/**
	 * Gets this builder's {@link #exception}.
	 *
	 * @return This builder's {@link #exception}.
	 */
	@Nullable
	public Exception getException() {
		return this.exception;
	}

	/**
	 * Sets this builder's {@link #exception}.
	 *
	 * @param exception An {@link Exception} to set.
	 * @return This builder.
	 */
	public CommandContextBuilder<E, C> setException(@Nullable Exception exception) {
		this.exception = exception;
		return this;
	}

	/**
	 * Gets this builder's {@link #arguments} {@link Map}.
	 *
	 * @return This builder's {@link #arguments} {@link Map}.
	 */
	public Map<Integer, ParsedArgument<?>> getArguments() {
		return this.arguments;
	}

	/**
	 * Adds a {@link ParsedArgument} to this builder.
	 *
	 * @param argument A {@link ParsedArgument} to add to this builder.
	 * @return This builder.
	 */
	public CommandContextBuilder<E, C> addArgument(ParsedArgument<?> argument) {
		this.arguments.put(this.nextArg++, argument);
		return this;
	}

	/**
	 * Gets a {@link ParsedArgument} at an index.
	 * <p>Can return null.</p>
	 *
	 * @param argument Index to get the {@link ParsedArgument} by.
	 * @param <A>      The type of the {@link ParsedArgument}'s return value.
	 * @return A {@link ParsedArgument} at an index.
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public <A> ParsedArgument<A> getArgument(int argument) {
		return (ParsedArgument<A>) this.arguments.get(argument);
	}

	/**
	 * Gets this builder's {@link #exceptionProvider}.
	 *
	 * @return This builder's {@link #exceptionProvider}.
	 */
	public BuiltInExceptionProvider getExceptionProvider() {
		return this.exceptionProvider;
	}

	/**
	 * Gets this builder's {@link #reader}.
	 *
	 * @return This builder's {@link #reader}.
	 */
	public MessageReader getReader() {
		return this.reader;
	}

	/**
	 * Builds this builder.
	 *
	 * @return A {@link CommandContext} constructed by this builder.
	 */
	public abstract C build();
}
