package net.smelly.disparser.properties;

import net.dv8tion.jda.api.Permission;
import net.smelly.disparser.annotations.Permissions;
import org.apache.commons.collections4.set.UnmodifiableSet;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link CommandProperty} implementation for command permissions.
 * <p>This class is immutable, meaning it's permissions can never be altered, thereby making it thread-safe.</p>
 *
 * @author Luke Tonon
 * @see CommandProperty
 */
@Immutable
public final class PermissionsProperty implements CommandProperty<UnmodifiableSet<Permission>, Permissions> {
	private final UnmodifiableSet<Permission> permissions;

	private PermissionsProperty(Set<Permission> permissions) {
		this.permissions = (UnmodifiableSet<Permission>) UnmodifiableSet.unmodifiableSet(new HashSet<>(permissions));
	}

	/**
	 * Creates a new {@link PermissionsProperty} instance with a default set of permissions.
	 *
	 * @param permissions The default {@link Set} of permissions.
	 * @return A new {@link PermissionsProperty} instance with a default set of permissions.
	 */
	public static PermissionsProperty create(Set<Permission> permissions) {
		return new PermissionsProperty(permissions);
	}

	/**
	 * Creates a new {@link PermissionsProperty} instance with a default array of permissions.
	 *
	 * @param permissions The default array of permissions.
	 * @return A new {@link PermissionsProperty} instance with a default array of permissions.
	 */
	public static PermissionsProperty create(Permission... permissions) {
		return new PermissionsProperty(new HashSet<>(Arrays.asList(permissions)));
	}

	/**
	 * Gets an {@link UnmodifiableSet} of permissions for this property when combined with a {@link Permissions} annotation.
	 *
	 * @param permissions The {@link Permissions} annotation to get the combined annotations for with this property.
	 * @return A {@link UnmodifiableSet} of permissions for this property when combined with a {@link Permissions} annotation.
	 */
	@Override
	public UnmodifiableSet<Permission> get(@Nullable Permissions permissions) {
		if (permissions != null) {
			Set<Permission> newPerms = new HashSet<>(this.permissions);
			if (permissions.mergePermissions()) {
				newPerms.addAll(Arrays.asList(permissions.value()));
			} else {
				newPerms = new HashSet<>(Arrays.asList(permissions.value()));
			}
			return (UnmodifiableSet<Permission>) UnmodifiableSet.unmodifiableSet(newPerms);
		}
		return this.permissions;
	}
}
