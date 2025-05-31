package com.tickettech.authservice.service;


import com.tickettech.authservice.dto.UserSaveDTO;
import com.tickettech.authservice.dto.output.ResultDTO;
import com.tickettech.authservice.models.Role;
import com.tickettech.authservice.models.User;

import java.util.List;

public interface UserService {
    ResultDTO saveUser(UserSaveDTO userSaveDTO);

    User save(User user);

    User findUserByUsername(String username);

    Role addRole(Role role);

    User addRoleToUser(String username, String roleName);

    User removeRoleToUser(String username, String roleName);

    void deleteUserById(Long id);

    List<User> getAllUsers();
}
