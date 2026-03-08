package kz.shyngys.springbootfilestorage.model.enumerated;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    ADMIN(Set.of(
            Permission.USERS_READ,
            Permission.USERS_WRITE,
            Permission.FILES_READ,
            Permission.FILES_UPLOAD,
            Permission.FILES_DELETE,
            Permission.EVENTS_READ,
            Permission.EVENTS_WRITE,
            Permission.EVENTS_DELETE
    )),
    MODERATOR(Set.of(
            Permission.USERS_READ,
            Permission.FILES_READ,
            Permission.FILES_UPLOAD,
            Permission.FILES_DELETE,
            Permission.EVENTS_READ,
            Permission.EVENTS_WRITE,
            Permission.EVENTS_DELETE
    )),
    USER(Set.of(
            Permission.FILES_READ,
            Permission.FILES_UPLOAD,
            Permission.EVENTS_READ
    ));

    private final Set<Permission> permissions;

    public Set<Permission> getPermissions() {
        return permissions;
    }

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}