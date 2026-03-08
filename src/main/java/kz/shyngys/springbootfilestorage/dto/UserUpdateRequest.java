package kz.shyngys.springbootfilestorage.dto;

import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String password;
    private UserRole role;
    private UserStatus status;
}