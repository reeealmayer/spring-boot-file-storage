package kz.shyngys.springbootfilestorage.model.enumerated;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    ADMIN(Set.of(Permission.USERS_READ, Permission.USERS_WRITE)),
    MODERATOR(Set.of(Permission.USERS_READ, Permission.USERS_WRITE)),
    USER(Set.of(Permission.USERS_READ, Permission.USERS_WRITE));

    private final Set<Permission> permissions;

    public Set<Permission> getPermissions() {
        return permissions;
    }

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
