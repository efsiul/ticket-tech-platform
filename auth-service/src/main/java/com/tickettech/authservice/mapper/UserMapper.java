package com.tickettech.authservice.mapper;


import com.tickettech.authservice.dto.UserDTO;
import com.tickettech.authservice.models.User;

public interface UserMapper {
    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);
}