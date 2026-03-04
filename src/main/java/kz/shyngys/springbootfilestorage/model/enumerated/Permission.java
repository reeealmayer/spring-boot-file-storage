package kz.shyngys.springbootfilestorage.model.enumerated;

public enum Permission {
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    FILES_UPLOAD("files:upload");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
