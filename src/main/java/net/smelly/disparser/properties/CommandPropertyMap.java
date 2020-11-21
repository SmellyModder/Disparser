package net.smelly.disparser.properties;

import net.dv8tion.jda.api.events.Event;
import net.smelly.disparser.Command;
import net.smelly.disparser.context.CommandContext;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for mappings values of {@link CommandProperty}s for {@link Command}s.
 * <p>You may design your own classes for mapping commands to property values if needed, but make sure the map is entirely thread-safe as Disparser is designed to be able to run concurrently.</p>
 *
 * @author Luke Tonon
 * @see CommandProperty
 * @see CommandProperty.Value
 * @see Command
 */
@ThreadSafe
public final class CommandPropertyMap<E extends Event, C extends CommandContext<E>> {
	private final ConcurrentHashMap<Command<E, C>, PropertyMap> commandPropertyMap = new ConcurrentHashMap<>();

	private CommandPropertyMap() {
	}

	/**
	 * Creates an empty {@link CommandPropertyMap} instance.
	 *
	 * @return An empty {@link CommandPropertyMap} instance.
	 */
	public static <E extends Event, C extends CommandContext<E>> CommandPropertyMap<E, C> createEmpty() {
		return new CommandPropertyMap<>();
	}

	/**
	 * Creates an {@link CommandPropertyMap} instance with a map put all onto it.
	 *
	 * @return An {@link CommandPropertyMap} instance with a map put all onto it.
	 */
	public static <E extends Event, C extends CommandContext<E>> CommandPropertyMap<E, C> create(Map<Command<E, C>, PropertyMap> map) {
		CommandPropertyMap<E, C> commandPropertyMap = new CommandPropertyMap<>();
		commandPropertyMap.commandPropertyMap.putAll(map);
		return commandPropertyMap;
	}

	/**
	 * Puts a {@link Command} onto the map with its default property values.
	 * <p>Ideally this method should only be called to reset a command's property values.</p>
	 *
	 * @param command The {@link Command} to put.
	 * @return A {@link PropertyMap} containing the default property values of the {@link Command}.
	 */
	public PropertyMap putCommand(Command<E, C> command) {
		PropertyMap propertyMap = this.getAndClearPropertyMap(command);
		for (CommandProperty<?, ?> property : command.getProperties()) {
			propertyMap.putUnsafe(property, property.get(null));
		}
		return propertyMap;
	}

	/**
	 * Very similar to {@link #putCommand(Command)} with the difference being the passed in annotation will get the command's default properties modified by that annotation.
	 * <p>This method will not alter the property values in the map if the passed in property isn't contained in the command.</p>
	 *
	 * @param command    The {@link Command} to put.
	 * @param property   The {@link CommandProperty} to put the annotation for.
	 * @param annotation The {@link Annotation} to put the modified default values of the {@link CommandProperty} for.
	 * @return A {@link PropertyMap} containing the default property values of the {@link Command}.
	 * @see #putCommand(Command)
	 */
	public <T, A extends Annotation, P extends CommandProperty<T, A>> void putCommandAnnotated(Command<E, C> command, P property, @Nullable A annotation) {
		Set<CommandProperty<?, ?>> properties = command.getProperties();
		if (properties.contains(property)) {
			PropertyMap propertyMap = this.getAndClearPropertyMap(command);
			for (CommandProperty<?, ?> commandProperty : properties) {
				if (commandProperty == property) {
					propertyMap.put(property, property.get(annotation));
				} else {
					propertyMap.putUnsafe(property, property.get(null));
				}
			}
		}
	}

	/**
	 * Gets or creates the {@link PropertyMap} for a given {@link Command}.
	 * <p>A new {@link PropertyMap} with default values is only ever created for the given command when there is no {@link PropertyMap} for the given command.</p>
	 *
	 * @param command The {@link Command} to get the {@link PropertyMap} for.
	 * @return A {@link PropertyMap} for a given {@link Command}.
	 */
	public PropertyMap getPropertyMap(Command<E, C> command) {
		return this.commandPropertyMap.computeIfAbsent(command, (key) -> {
			PropertyMap propertyMap = new PropertyMap();
			for (CommandProperty<?, ?> property : command.getProperties()) {
				propertyMap.putUnsafe(property, property.get(null));
			}
			return propertyMap;
		});
	}

	/**
	 * Gets and clears a {@link PropertyMap} for a given {@link Command}.
	 * <p>It is recommended to only use this when resetting the properties of a given {@link Command}.</p>
	 *
	 * @param command The {@link Command} to get the cleared {@link PropertyMap} for.
	 * @return An empty {@link PropertyMap} for a given {@link Command}.
	 */
	public PropertyMap getAndClearPropertyMap(Command<E, C> command) {
		PropertyMap propertyMap = this.getPropertyMap(command);
		propertyMap.clear();
		return propertyMap;
	}

	/**
	 * Gets the {@link CommandProperty.Value} for a given {@link Command} and {@link CommandProperty}.
	 * <p>This should only be used to get or modify the {@link CommandProperty.Value} for a given {@link Command} and {@link CommandProperty}.</p>
	 *
	 * @param command  The command to get the {@link CommandProperty.Value} for.
	 * @param property The {@link CommandProperty} belonging to the {@link Command} to get the {@link CommandProperty.Value} for.
	 * @param <T>      The type of the value.
	 * @param <P>      The type of the property.
	 * @return The {@link CommandProperty.Value} for a given {@link Command} and {@link CommandProperty}.
	 */
	public <T, P extends CommandProperty<T, ?>> CommandProperty.Value<T> getPropertyValue(Command<E, C> command, P property) {
		return this.getPropertyMap(command).get(property);
	}

	/**
	 * A thread-safe class for mapping the values of {@link CommandProperty}s.
	 * Use {@link PropertyMap#get(CommandProperty)} instead of the other methods when possible.
	 * <p>If you wish to make further use of the map inside this class extend it to add your own needs.</p>
	 *
	 * @author Luke Tonon
	 */
	@ThreadSafe
	public static class PropertyMap {
		protected final ConcurrentHashMap<CommandProperty<?, ?>, CommandProperty.Value<?>> propertyMap = new ConcurrentHashMap<>();

		/**
		 * Creates a new {@link PropertyMap} instance with initial values of another map.
		 *
		 * @param addMap The map to add onto this map.
		 * @return A new {@link PropertyMap} instance with initial values of another map.
		 */
		public static PropertyMap create(Map<CommandProperty<?, ?>, CommandProperty.Value<?>> addMap) {
			PropertyMap propertyMap = new PropertyMap();
			propertyMap.putAll(addMap);
			return propertyMap;
		}

		/**
		 * Assigns a new value to a {@link CommandProperty.Value} for to a {@link CommandProperty}.
		 * <p>For the dynamic version of this method, use {@link #putUnsafe(CommandProperty, Object)}</p>
		 *
		 * @param commandProperty The {@link CommandProperty} to modify the mapped {@link CommandProperty.Value} for.
		 * @param value           The new value to set the value inside the {@link CommandProperty.Value} to.
		 * @param <T>             The type of the value.
		 * @param <A>             The annotation of the {@link CommandProperty}.
		 * @param <P>             The type of the {@link CommandProperty} to modify the mapped {@link CommandProperty.Value} for.
		 */
		public <T, A extends Annotation, P extends CommandProperty<T, A>> void put(P commandProperty, T value) {
			this.putOrModify(commandProperty, value);
		}

		/**
		 * Unsafely assigns a value to a {@link CommandProperty.Value} for to a {@link CommandProperty}.
		 * <p>Unsafe meaning the value to set is not guaranteed to match the type of value for the passed in {@link CommandProperty}</p>
		 * <p>Use {@link #put(CommandProperty, Object)} instead of this method when possible.</p>
		 *
		 * @param commandProperty The {@link CommandProperty} to modify the mapped {@link CommandProperty.Value} for.
		 * @param value           The new value to set the value inside the {@link CommandProperty.Value} to.
		 * @see #put(CommandProperty, Object)
		 */
		protected void putUnsafe(CommandProperty<?, ?> commandProperty, Object value) {
			this.putOrModify(commandProperty, value);
		}

		/**
		 * Safely puts a map of properties to values onto this map.
		 *
		 * @param map The map to put onto this map.
		 * @throws ClassCastException If the value of a {@link CommandProperty.Value} isn't an instance of the return value of one of the properties.
		 */
		public void putAll(Map<CommandProperty<?, ?>, CommandProperty.Value<?>> map) {
			map.forEach((commandProperty, value) -> {
				if (commandProperty.get(null).getClass().isInstance(value.get())) {
					this.putOrModify(commandProperty, value);
				} else {
					throw new ClassCastException(String.format("Property type doesn't match value type! Property: %1$s, Value: %2$s", commandProperty, value));
				}
			});
		}

		/**
		 * Sets a new value for a {@link CommandProperty.Value} mapped to a {@link CommandProperty}.
		 * <p>If no {@link CommandProperty.Value} is mapped for the specific {@link CommandProperty} it will compute a default one to the map.</p>
		 *
		 * @param commandProperty The {@link CommandProperty} to lookup a {@link CommandProperty.Value} to modify.
		 * @param value           The new value to set.
		 * @return This instance.
		 */
		public PropertyMap putOrModify(CommandProperty<?, ?> commandProperty, Object value) {
			this.propertyMap.computeIfAbsent(commandProperty, (key) -> CommandProperty.Value.create(commandProperty)).unsafeSet(value);
			return this;
		}

		/**
		 * Gets a {@link CommandProperty.Value} mapped to {@link CommandProperty}.
		 * <p>When possible, this method should be used instead of the other methods in this class to modify a value.</p>
		 *
		 * @param commandProperty The {@link CommandProperty} to get the {@link CommandProperty.Value} mapped to it.
		 * @param <T>             The type of the value.
		 * @param <P>             The type of the {@link CommandProperty}.
		 * @return A {@link CommandProperty.Value} mapped to {@link CommandProperty}.
		 */
		@SuppressWarnings("unchecked")
		public <T, P extends CommandProperty<T, ?>> CommandProperty.Value<T> get(P commandProperty) {
			return (CommandProperty.Value<T>) this.propertyMap.computeIfAbsent(commandProperty, (key) -> CommandProperty.Value.create(commandProperty));
		}

		/**
		 * Clears the map.
		 */
		public void clear() {
			this.propertyMap.clear();
		}
	}
}
