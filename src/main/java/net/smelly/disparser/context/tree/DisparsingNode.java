package net.smelly.disparser.context.tree;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.MessageReader;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.CommandContextBuilder;
import net.smelly.disparser.context.ContextConsumer;
import net.smelly.disparser.context.Requirement;
import net.smelly.disparser.feedback.CommandMessage;
import net.smelly.disparser.feedback.exceptions.CommandException;
import org.apache.commons.collections4.map.UnmodifiableMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An immutable node class that represents an argument to be disparsed.
 * <p>These nodes make up trees that can be disparsed from a {@link net.dv8tion.jda.api.entities.Message} to build a {@link CommandContext}</p>
 *
 * @param <E> The type of {@link Event} this node disparses for.
 * @param <C> The type of {@link CommandContext} this node builds for.
 * @author Luke Tonon
 * @see MessageReader
 * @see CommandContextBuilder
 * @see DisparsingNodeBuilder
 */
@Immutable
public abstract class DisparsingNode<E extends Event, C extends CommandContext<E>> {
	private final Map<CommandMessage, DisparsingNode<E, C>> children;
	private final CommandMessage name;
	private final Requirement<E> requirement;
	@Nullable
	private final ContextConsumer<C> consumer;

	public DisparsingNode(LinkedHashMap<CommandMessage, DisparsingNode<E, C>> children, CommandMessage name, Requirement<E> requirement, @Nullable ContextConsumer<C> consumer) {
		this.children = UnmodifiableMap.unmodifiableMap(new LinkedHashMap<>(children));
		this.name = name;
		this.requirement = requirement;
		this.consumer = consumer;
	}

	/**
	 * Disparses this node. An exception is thrown during this method when this node has <b>failed</b> to disparse for the given {@link CommandContextBuilder} and {@link MessageReader}.
	 * <p>Consequently, if this node were to thrown an exception, then its child nodes should not be disparsed.</p>
	 *
	 * @param builder A {@link CommandContextBuilder} to disparse this node for.
	 * @param reader  A {@link MessageReader} for reading the next argument.
	 * @throws CommandException When the disparsing for this node has failed.
	 */
	public abstract void disparse(CommandContextBuilder<E, C> builder, MessageReader reader) throws CommandException;

	/**
	 * Gets this node's child nodes.
	 *
	 * @return An unmodifiable {@link Collection} of the values in this node's child map.
	 */
	public Collection<DisparsingNode<E, C>> getChildren() {
		return this.children.values();
	}

	/**
	 * Gets the name of this node for a given {@link MessageChannel}.
	 *
	 * @param channel A {@link MessageChannel} to get this node's name for.
	 * @return The name of this node for a given {@link MessageChannel}.
	 */
	public String getName(@Nullable MessageChannel channel) {
		return this.name.getMessage(channel);
	}

	/**
	 * Gets this node's {@link #name}.
	 *
	 * @return The {@link CommandMessage} name of this node.
	 */
	public CommandMessage getName() {
		return this.name;
	}

	/**
	 * Gets this node's {@link Requirement}.
	 *
	 * @return This node's {@link Requirement}.
	 */
	public Requirement<E> getRequirement() {
		return this.requirement;
	}

	/**
	 * Gets this node's {@link #consumer}. This can return null.
	 * <p>When this returns null, child nodes of this node that fail to parse should not return to this node to consume.</p>
	 *
	 * @return This node's {@link #consumer}.
	 */
	@Nullable
	public ContextConsumer<C> getConsumer() {
		return this.consumer;
	}
}
