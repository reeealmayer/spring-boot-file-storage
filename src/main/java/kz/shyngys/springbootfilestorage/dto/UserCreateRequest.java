package kz.shyngys.springbootfilestorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.shyngys.springbootfilestorage.model.enumerated.UserRole;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private UserRole role;
}