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
 * A {@link DisparsingNode} extension representing a sub-command as an argument.
 *
 * @param <E> The type of {@link Event} this node disparses for.
 * @param <C> The type of {@link CommandContext} this node builds for.
 * @author Luke Tonon
 * @see DisparsingNode
 */
@Immutable
public final class CommandNode<E extends Event, C extends CommandContext<E>> extends DisparsingNode<E, C> {

	public CommandNode(LinkedHashMap<CommandMessage, DisparsingNode<E, C>> children, CommandMessage name, Requirement<E> requirement, ContextConsumer<C> consumer) {
		super(children, name, requirement, consumer);
	}

	/**
	 * Disparses this {@link CommandNode} for a given {@link CommandContextBuilder} and {@link MessageReader}.
	 *
	 * @param builder A {@link CommandContextBuilder} to disparse this node for.
	 * @param reader  A {@link MessageReader} for reading the next argument.
	 * @throws CommandException If there was no next argument or the next argument was invalid.
	 */
	@Override
	public void disparse(CommandContextBuilder<E, C> builder, MessageReader reader) throws CommandException {
		String nextArg = reader.nextArgument();
		if (nextArg.equalsIgnoreCase(this.getName(reader.getMessage().getChannel()))) {
			builder.setConsumer(this.getConsumer());
		} else {
			throw builder.getExceptionProvider().getInvalidCommandArgumentException().create(nextArg, reader.getIndex());
		}
	}

	@Override
	public String toString() {
		return "CommandNode{" +
				"name=" + this.getName(null) +
				", children=" + this.getChildren().size() +
				'}';
	}

	/**
	 * A {@link DisparsingNodeBuilder} extension for building {@link CommandNode}s.
	 *
	 * @param <E> The type of {@link Event} this builds for.
	 * @param <C> The type of {@link CommandContext} this builds for.
	 * @author Luke Tonon
	 * @see CommandNode
	 * @see DisparsingNodeBuilder
	 */
	@NotThreadSafe
	public static class Builder<E extends Event, C extends CommandContext<E>> extends DisparsingNodeBuilder<E, C, Builder<E, C>> {

		private Builder(CommandMessage name) {
			super(name);
		}

		/**
		 * Creates a new {@link Builder} with a specified {@link CommandMessage} name.
		 *
		 * @param name A {@link CommandMessage} name of the node this builder builds.
		 * @param <E>  The type of {@link Event} this builds for.
		 * @param <C>  The type of {@link CommandContext} this builds for.
		 * @return a new {@link Builder} with a specified {@link CommandMessage} name.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> named(CommandMessage name) {
			return new Builder<>(name);
		}

		/**
		 * Creates a new {@link Builder} with a specified {@link CommandMessage} name.
		 * <p>Very similar to {@link #named(CommandMessage)} with the difference being this takes in a {@link Class} to specify what type C is.</p>
		 *
		 * @param name A {@link CommandMessage} name of the node this builder builds.
		 * @param <E>  The type of {@link Event} this builds for.
		 * @param <C>  The type of {@link CommandContext} this builds for.
		 * @return a new {@link Builder} with a specified {@link CommandMessage} name.
		 */
		public static <E extends Event, C extends CommandContext<E>> Builder<E, C> named(CommandMessage name, Class<C> contextClass) {
			return new Builder<>(name);
		}

		/**
		 * Builds this builder.
		 *
		 * @return A constructed {@link CommandNode}.
		 */
		@Override
		public CommandNode<E, C> build() {
			return new CommandNode<>(DisparsingNodeBuilder.buildMap(this.children), this.name, this.requirement, this.consumer);
		}

	}

}
