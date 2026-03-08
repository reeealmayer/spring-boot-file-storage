package kz.shyngys.springbootfilestorage.dto;

import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import kz.shyngys.springbootfilestorage.model.enumerated.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    Long id;
    String username;
    UserRole role;
    UserStatus status;
}
