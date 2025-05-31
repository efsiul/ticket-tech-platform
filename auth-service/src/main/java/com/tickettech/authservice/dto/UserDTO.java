package com.tickettech.authservice.dto;

import com.tickettech.authservice.models.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long id;
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "email cannot be null")
    private String email;
    private String password;
    private Boolean enabled;
    private List<Role> roles;

}
