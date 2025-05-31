package com.tickettech.authservice.mapper.implementation;

import com.tickettech.authservice.dto.UserDTO;
import com.tickettech.authservice.mapper.UserMapper;
import com.tickettech.authservice.models.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserMappersImpl implements UserMapper {

    private final ModelMapper modelMapper;

    public UserMappersImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO toDTO(User user) {
        return (user != null) ? modelMapper.map(user, UserDTO.class) : null;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        return (userDTO != null) ? modelMapper.map(userDTO, User.class) : null;
    }
}
