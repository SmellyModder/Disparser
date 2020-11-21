package net.smelly.disparser;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.context.ContextConsumer;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.exceptions.CommandException;
import net.smelly.disparser.properties.AliasesProperty;
import net.smelly.disparser.properties.CommandProperty;
import net.smelly.disparser.properties.PermissionsProperty;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for a command.
 * <p>All fields in this class are unmodifiable for thread-safety. Extensions of this class that add more fields should also be immutable for respect to thread-safety.</p>
 *
 * @author Luke Tonon
 */
@Immutable
public class Command<E extends Event, C extends CommandContext<E>> {
	private final AliasesProperty aliasesProperty;
	private final PermissionsProperty permissionsProperty;
	private final UnmodifiableSet<CommandProperty<?, ?>> properties;
	private final RootNode<E, C> rootNode;

	public Command(String name, RootNode<E, C> rootNode) {
		this(new HashSet<>(Collections.singletonList(name)), new HashSet<>(Arrays.asList(Permission.EMPTY_PERMISSIONS)), rootNode);
	}

	public Command(Set<String> aliases, Set<Permission> permissions, RootNode<E, C> rootNode) {
		this.aliasesProperty = AliasesProperty.create(aliases);
		this.permissionsProperty = PermissionsProperty.create(permissions);
		Set<CommandProperty<?, ?>> properties = new HashSet<>();
		properties.add(this.aliasesProperty);
		properties.add(this.permissionsProperty);
		this.properties = (UnmodifiableSet<CommandProperty<?, ?>>) UnmodifiableSet.unmodifiableSet(properties);
		this.rootNode = rootNode;
	}

	public Command(AliasesProperty aliasesProperty, PermissionsProperty permissionsProperty, Set<CommandProperty<?, ?>> properties, RootNode<E, C> rootNode) {
		this.aliasesProperty = aliasesProperty;
		this.permissionsProperty = permissionsProperty;
		this.properties = (UnmodifiableSet<CommandProperty<?, ?>>) UnmodifiableSet.unmodifiableSet(properties);
		this.rootNode = rootNode;
	}

	/**
	 * Used for processing this command.
	 * <b>Override this if you wish to do manual things</b>
	 *
	 * @param context The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the event stored in the {@link CommandContext}.
	 */
	public void processCommand(C context, ContextConsumer<C> consumer) throws CommandException {
		consumer.accept(context);
	}

	/**
	 * Gets this command's {@link AliasesProperty}.
	 *
	 * @return This command's {@link AliasesProperty}.
	 */
	public AliasesProperty getAliasesProperty() {
		return this.aliasesProperty;
	}

	/**
	 * Gets this command's {@link PermissionsProperty}.
	 *
	 * @return This command's {@link PermissionsProperty}.
	 */
	public PermissionsProperty getPermissionsProperty() {
		return this.permissionsProperty;
	}

	/**
	 * Gets this command's properties.
	 *
	 * @return This's command's properties.
	 */
	public UnmodifiableSet<CommandProperty<?, ?>> getProperties() {
		return this.properties;
	}

	/**
	 * Gets this command's {@link #rootNode}.
	 *
	 * @return This command's {@link RootNode}.
	 */
	public final RootNode<E, C> getRootNode() {
		return this.rootNode;
	}

	@Override
	public String toString() {
		return "Command{" +
				"aliasesProperty=" + this.aliasesProperty +
				", permissionsProperty=" + this.permissionsProperty +
				", properties=" + this.properties +
				", rootNode=" + this.rootNode +
				'}';
	}
}
