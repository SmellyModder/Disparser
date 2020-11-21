package net.smelly.disparser.context.tree;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.CommandContextBuilder;
import net.smelly.disparser.context.ContextConsumer;
import net.smelly.disparser.context.Requirement;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandException;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.LinkedHashMap;

/**
 * A {@link DisparsingNode} extension representing the starting node of a {@link net.smelly.disparser.Command}.
 *
 * @param <E> The type of {@link Event} this node disparses for.
 * @param <C> The type of {@link CommandContext} this node builds for.
 * @author Luke Tonon
 * @see DisparsingNode
 */
@Immutable
public final class RootNode<E extends Event, C extends CommandContext<E>> extends DisparsingNode<E, C> {

	public RootNode(LinkedHashMap<CommandMessage, DisparsingNode<E, C>> children, Requirement<E> requirement, ContextConsumer<C> consumer) {
		super(children, CommandMessage.EMPTY, requirement, consumer);
	}

	/**
	 * Disparses this {@link RootNode} for a given {@link CommandContextBuilder} and {@link MessageReader}.
	 *
	 * @param builder A {@link CommandContextBuilder} to disparse this node for.
	 * @param reader  A {@link MessageReader} for reading the next argument.
	 * @throws CommandException If there was no next argument for this node's children and this node has no consumer.
	 */
	@Override
	public void disparse(CommandContextBuilder<E, C> builder, MessageReader reader) throws CommandException {
		ContextConsumer<C> consumer = this.getConsumer();
		builder.setConsumer(consumer);
		if (consumer == null && !this.getChildren().isEmpty() && !reader.hasNextArg()) {
			throw builder.getExceptionProvider().getNoArgumentsException().create();
		}
	}

	@Override
	public String toString() {
		return "RootNode{children=" + this.getChildren().size() + '}';
	}

	/**
	 * A {@link DisparsingNodeBuilder} extension for building {@link RootNode}s.
	 *
	 * @param <E> The type of {@link Event} this builds for.
	 * @param <C> The type of {@link CommandContext} node builds for.
	 * @author Luke Tonon
	 * @see RootNode
	 * @see DisparsingNodeBuilder
	 */
	@NotThreadSafe
	public static class Builder<E extends Event, C extends CommandContext<E>> extends DisparsingNodeBuilder<E, C, Builder<E, C>> {

		private Builder() {
			super(CommandMessage.EMPTY);
		}

		/**
		 * Creates a new {@link Builder}.
		 *
		 * @param <E> The type of {@link Event} this builds for.
		 * @param <C> The type of {@link CommandContext} node builds for.
		 * @return A new {@link Builder}.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> create() {
			return new Builder<>();
		}

		/**
		 * Creates a new {@link Builder}.
		 * <p>Very similar to {@link #create()} with the difference being this takes in a {@link Class} to specify what type C is.</p>
		 *
		 * @param <E> The type of {@link Event} this builds for.
		 * @param <C> The type of {@link CommandContext} node builds for.
		 * @return A new {@link Builder}.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> create(Class<C> contextClass) {
			return new Builder<>();
		}

		/**
		 * Builds this builder.
		 *
		 * @return A constructed {@link RootNode}.
		 */
		@Override
		public RootNode<E, C> build() {
			return new RootNode<>(DisparsingNodeBuilder.buildMap(this.children), this.requirement, this.consumer);
		}

	}

}
