package net.smelly.disparser.context.tree;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.ConfiguredArgument;
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
 * A {@link DisparsingNode} extension representing an {@link ConfiguredArgument}.
 *
 * @param <E> The type of {@link Event} this node disparses for.
 * @param <C> The type of {@link CommandContext} this node builds for.
 * @author Luke Tonon
 * @see DisparsingNode
 */
@Immutable
public final class ArgumentNode<E extends Event, C extends CommandContext<E>> extends DisparsingNode<E, C> {
	private final ConfiguredArgument<?> argument;

	public ArgumentNode(LinkedHashMap<CommandMessage, DisparsingNode<E, C>> children, ConfiguredArgument<?> argument, Requirement<E> requirement, ContextConsumer<C> consumer) {
		super(children, argument.getName(), requirement, consumer);
		this.argument = argument;
	}

	/**
	 * Disparses this {@link ArgumentNode} for a given {@link CommandContextBuilder} and {@link MessageReader}.
	 *
	 * @param builder A {@link CommandContextBuilder} to disparse this node for.
	 * @param reader  A {@link MessageReader} for reading the next argument.
	 * @throws CommandException If there was no next argument or the next argument failed to be parsed by this node's internal {@link #argument}.
	 */
	@Override
	public void disparse(CommandContextBuilder<E, C> builder, MessageReader reader) throws CommandException {
		builder.addArgument(this.argument.parse(reader));
		if (this.getConsumer() != null) {
			builder.setConsumer(this.getConsumer());
		}
	}

	@Override
	public String toString() {
		return "ArgumentNode{" +
				"name=" + this.getName(null) +
				", children=" + this.getChildren().size() +
				", argument=" + this.argument +
				'}';
	}

	/**
	 * A {@link DisparsingNodeBuilder} extension for building {@link ArgumentNode}s.
	 *
	 * @param <E> The type of {@link Event} this builds for.
	 * @param <C> The type of {@link CommandContext} this builds for.
	 * @author Luke Tonon
	 * @see ArgumentNode
	 * @see DisparsingNodeBuilder
	 */
	@NotThreadSafe
	public static class Builder<E extends Event, C extends CommandContext<E>> extends DisparsingNodeBuilder<E, C, Builder<E, C>> {
		private final ConfiguredArgument<?> argument;

		private Builder(ConfiguredArgument<?> argument) {
			super(argument.getName());
			this.argument = argument;
		}

		/**
		 * Creates a new {@link Builder} with a specified {@link ConfiguredArgument}.
		 *
		 * @param argument A {@link ConfiguredArgument} to use for building a {@link ArgumentNode}.
		 * @param <E>      The type of {@link Event} this node disparses for.
		 * @param <C>      The type of {@link CommandContext} this node builds for.
		 * @return a new {@link Builder} with a specified {@link CommandMessage} name.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> create(ConfiguredArgument<?> argument) {
			return new Builder<>(argument);
		}

		/**
		 * Creates a new {@link Builder} with a specified {@link ConfiguredArgument}.
		 * <p>Very similar to {@link #create(ConfiguredArgument)} with the difference being this takes in a {@link Class} to specify what type C is.</p>
		 *
		 * @param argument A {@link ConfiguredArgument} to use for building a {@link ArgumentNode}.
		 * @param <E>      The type of {@link Event} this builds for.
		 * @param <C>      The type of {@link CommandContext} this builds for.
		 * @return a new {@link Builder} with a specified {@link CommandMessage} name.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> create(ConfiguredArgument<?> argument, Class<C> contextClass) {
			return new Builder<>(argument);
		}

		/**
		 * Builds this builder.
		 *
		 * @return A constructed {@link ArgumentNode}.
		 */
		@Override
		public ArgumentNode<E, C> build() {
			return new ArgumentNode<>(DisparsingNodeBuilder.buildMap(this.children), this.argument, this.requirement, this.consumer);
		}
	}
}
