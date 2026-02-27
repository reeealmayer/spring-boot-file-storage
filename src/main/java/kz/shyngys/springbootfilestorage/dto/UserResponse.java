package kz.shyngys.springbootfilestorage.dto;

import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;

public record UserResponse(
        Long id,
        String username,
        UserRole role,
        UserStatus status
) {
}
