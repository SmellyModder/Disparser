package net.smelly.disparser;

import net.dv8tion.jda.api.Permission;
import net.smelly.disparser.annotations.Optional;
import net.smelly.disparser.context.CommandContext;
import net.smelly.disparser.properties.AliasesProperty;
import net.smelly.disparser.properties.CommandProperty;
import net.smelly.disparser.properties.PermissionsProperty;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.concurrent.Immutable;
import java.util.*;

/**
 * Abstract class for a command.
 * <p>All fields in this class are unmodifiable for thread-safety. Extensions of this class that add more fields should also be immutable for respect to thread-safety.</p>
 *
 * @author Luke Tonon
 */
@Immutable
public abstract class Command<C extends CommandContext<?>> {
	private final AliasesProperty aliasesProperty;
	private final PermissionsProperty permissionsProperty;
	private final UnmodifiableSet<CommandProperty<?, ?>> properties;
	private final UnmodifiableList<Argument<?>> arguments;

	public Command(String name) {
		this(name, new Argument[0]);
	}

	public Command(String name, Argument<?>... args) {
		this(new HashSet<>(Collections.singletonList(name)), new HashSet<>(Arrays.asList(Permission.EMPTY_PERMISSIONS)), args);
	}

	public Command(Set<String> aliases, Set<Permission> permissions, Argument<?>... args) {
		this.aliasesProperty = AliasesProperty.create(aliases);
		this.permissionsProperty = PermissionsProperty.create(permissions);
		List<Argument<?>> setupArguments = new ArrayList<>();
		for (Argument<?> argument : args) {
			setupArguments.add(argument.getClass().isAnnotationPresent(Optional.class) ? argument.asOptional() : argument);
		}
		Set<CommandProperty<?, ?>> properties = new HashSet<>();
		properties.add(this.aliasesProperty);
		properties.add(this.permissionsProperty);
		this.properties = (UnmodifiableSet<CommandProperty<?, ?>>) UnmodifiableSet.unmodifiableSet(properties);
		this.arguments = new UnmodifiableList<>(setupArguments);
	}

	public Command(AliasesProperty aliasesProperty, PermissionsProperty permissionsProperty, List<Argument<?>> arguments, Set<CommandProperty<?, ?>> properties) {
		this.aliasesProperty = aliasesProperty;
		this.permissionsProperty = permissionsProperty;
		this.arguments = (UnmodifiableList<Argument<?>>) UnmodifiableList.unmodifiableList(arguments);
		this.properties = (UnmodifiableSet<CommandProperty<?, ?>>) UnmodifiableSet.unmodifiableSet(properties);
	}

	/**
	 * Used for processing this command.
	 *
	 * @param context The {@link CommandContext} for this command, use this to get the parsed arguments and make use of the event stored in the {@link CommandContext}.
	 */
	public abstract void processCommand(C context) throws Exception;

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
	 * Gets this's command's properties.
	 *
	 * @return This's command's properties.
	 */
	public UnmodifiableSet<CommandProperty<?, ?>> getProperties() {
		return this.properties;
	}

	/**
	 * @return This command's arguments.
	 */
	public final UnmodifiableList<Argument<?>> getArguments() {
		return this.arguments;
	}
}