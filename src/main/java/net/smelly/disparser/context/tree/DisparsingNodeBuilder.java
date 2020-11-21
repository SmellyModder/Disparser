package net.smelly.disparser.context.tree;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.ContextConsumer;
import net.smelly.disparser.context.Requirement;
import net.smelly.disparser.feedback.CommandMessage;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A builder class for constructing a tree of {@link DisparsingNode}s.
 * <p>This class is mutable in a variety of ways, making it not thread-safe, and should not be reused in a multi-threaded environment.</p>
 *
 * @param <E> The type of {@link Event} for this builder.
 * @param <C> The type of {@link CommandContext} for this builder.
 * @param <B> The type of {@link DisparsingNodeBuilder} of this builder.
 * @author Luke Tonon
 */
@NotThreadSafe
public abstract class DisparsingNodeBuilder<E extends Event, C extends CommandContext<E>, B extends DisparsingNodeBuilder<E, C, B>> implements Comparable<DisparsingNodeBuilder<E, C, ?>> {
	protected final CommandMessage name;
	protected Map<CommandMessage, DisparsingNodeBuilder<E, C, ?>> children = new LinkedHashMap<>();
	protected Requirement<E> requirement = Requirement.none();
	protected ContextConsumer<C> consumer;

	public DisparsingNodeBuilder(CommandMessage name) {
		this.name = name;
	}

	/**
	 * Builds a map of builders and its child builders.
	 *
	 * @param map The map to build from.
	 * @param <E> The type of {@link Event} for this builder.
	 * @param <C> The type of {@link CommandContext} this builder will build.
	 * @param <N> The type of {@link DisparsingNode} this builder builds.
	 * @param <B> The type of {@link DisparsingNodeBuilder}.
	 * @return A map of built builders and its child builders.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Event, C extends CommandContext<E>, N extends DisparsingNode<E, C>, B extends DisparsingNodeBuilder<E, C, ?>> LinkedHashMap<CommandMessage, N> buildMap(Map<CommandMessage, B> map) {
		LinkedHashMap<CommandMessage, N> builtMap = new LinkedHashMap<>();
		for (Map.Entry<CommandMessage, B> entry : map.entrySet()) {
			builtMap.put(entry.getKey(), (N) entry.getValue().build());
		}
		return builtMap;
	}

	/**
	 * Builds this builder.
	 *
	 * @return The {@link DisparsingNode} this builder has built.
	 */
	public abstract DisparsingNode<E, C> build();

	/**
	 * Adds a child builder to this builder.
	 *
	 * @param builder A {@link DisparsingNodeBuilder} to add as a child.
	 * @return This builder.
	 * @see #addChild(DisparsingNodeBuilder).
	 */
	@SuppressWarnings("unchecked")
	public B then(DisparsingNodeBuilder<E, C, ?> builder) {
		this.addChild(builder);
		return (B) this;
	}

	/**
	 * Sets a {@link Requirement} for this builder.
	 *
	 * @param requirement A {@link Requirement} to set for this builder.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B requires(Requirement<E> requirement) {
		this.requirement = requirement;
		return (B) this;
	}

	/**
	 * Sets a {@link ContextConsumer} for this builder.
	 *
	 * @param consumer A {@link ContextConsumer} to set for this builder.
	 * @return This builder.
	 */
	@SuppressWarnings("unchecked")
	public B consumes(ContextConsumer<C> consumer) {
		this.consumer = consumer;
		return (B) this;
	}

	/**
	 * Adds a child {@link DisparsingNodeBuilder} to this builder.
	 * <p>If a child with the same {@link CommandMessage} is already present in the map it will merge onto that child.</p>
	 * <p>The children map is sorted using this node's {@link #compareTo(DisparsingNodeBuilder)} method after a child builder is added.</p>
	 *
	 * @param builder A {@link DisparsingNodeBuilder} to add as a child to this builder.
	 */
	protected void addChild(DisparsingNodeBuilder<E, C, ?> builder) {
		CommandMessage name = builder.name;
		DisparsingNodeBuilder<E, C, ?> child = this.children.get(name);
		if (child != null) {
			if (builder.consumer != null) {
				child.consumer = builder.consumer;
			}
			for (DisparsingNodeBuilder<E, C, ?> grandchild : builder.children.values()) {
				child.addChild(grandchild);
			}
		} else {
			this.children.put(name, builder);
		}
		this.children = this.children.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (builder1, builder2) -> builder1, LinkedHashMap::new));
	}

	@Override
	public int compareTo(@Nonnull DisparsingNodeBuilder<E, C, ?> other) {
		boolean isOtherCommand = other instanceof CommandNode.Builder;
		if (this instanceof CommandNode.Builder == isOtherCommand) {
			return this.name.getMessage(null).compareTo(other.name.getMessage(null));
		}
		return isOtherCommand ? 1 : -1;
	}
}
