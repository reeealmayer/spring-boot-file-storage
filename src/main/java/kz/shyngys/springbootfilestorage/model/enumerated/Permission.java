package kz.shyngys.springbootfilestorage.model.enumerated;

public enum Permission {
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    FILES_READ("files:read"),
    FILES_UPLOAD("files:upload"),
    FILES_DELETE("files:delete"),
    EVENTS_READ("events:read"),
    EVENTS_WRITE("events:write"),
    EVENTS_DELETE("events:delete");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}