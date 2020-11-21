package net.smelly.disparser.properties;

import net.smelly.disparser.annotations.Aliases;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link CommandProperty} implementation for command aliases.
 * <p>This class is immutable, meaning it's aliases can never be altered, thereby making it thread-safe.</p>
 *
 * @author Luke Tonon
 * @see CommandProperty
 */
@Immutable
public final class AliasesProperty implements CommandProperty<UnmodifiableSet<String>, Aliases> {
	private final UnmodifiableSet<String> aliases;

	private AliasesProperty(Set<String> aliases) {
		this.aliases = (UnmodifiableSet<String>) UnmodifiableSet.unmodifiableSet(new HashSet<>(aliases));
	}

	/**
	 * Creates a new {@link AliasesProperty} instance with a default set of aliases.
	 *
	 * @param aliases The default {@link Set} of aliases.
	 * @return A new {@link AliasesProperty} instance with a default set of aliases.
	 */
	public static AliasesProperty create(Set<String> aliases) {
		return new AliasesProperty(aliases);
	}

	/**
	 * Creates a new {@link AliasesProperty} instance with a default array of aliases.
	 *
	 * @param aliases The default array of aliases.
	 * @return A new {@link AliasesProperty} instance with a default array of aliases.
	 */
	public static AliasesProperty create(String... aliases) {
		return new AliasesProperty(new HashSet<>(Arrays.asList(aliases)));
	}

	/**
	 * Gets an {@link UnmodifiableSet} of aliases for this property when combined with an {@link Aliases} annotation.
	 *
	 * @param aliases The {@link Aliases} annotation to get the combined annotations for with this property.
	 * @return A {@link UnmodifiableSet} of aliases for this property when combined with an {@link Aliases} annotation.
	 */
	@Override
	public UnmodifiableSet<String> get(@Nullable Aliases aliases) {
		if (aliases != null) {
			Set<String> newAliases = new HashSet<>(this.aliases);
			if (aliases.mergeAliases()) {
				newAliases.addAll(Arrays.asList(aliases.value()));
			} else {
				newAliases = new HashSet<>(Arrays.asList(aliases.value()));
			}
			return (UnmodifiableSet<String>) UnmodifiableSet.unmodifiableSet(newAliases);
		}
		return this.aliases;
	}

	@Override
	public String toString() {
		return "AliasesProperty{" +
				"aliases=" + this.aliases +
				'}';
	}
}
