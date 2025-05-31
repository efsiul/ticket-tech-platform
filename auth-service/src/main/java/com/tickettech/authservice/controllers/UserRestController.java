package com.tickettech.authservice.controllers;


import com.tickettech.authservice.dto.Form;
import com.tickettech.authservice.dto.UserSaveDTO;
import com.tickettech.authservice.dto.output.ResultDTO;
import com.tickettech.authservice.models.Role;
import com.tickettech.authservice.models.User;
import com.tickettech.authservice.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRestController {

    private final UserService service;

    public UserRestController(UserService service) {
        this.service = service;
    }

    @PostMapping(value = "/save/user")
    public ResultDTO saveUser(@RequestBody UserSaveDTO userSaveDTO) throws Exception {
        return service.saveUser(userSaveDTO);
    }

    @GetMapping("/get/user/{username}")
    public User findUserByUsername(@PathVariable String username) {
        User user = service.findUserByUsername(username);
        return hideUserPassword(user);
    }

    @PostMapping("/save/role")
    public Role addRole(@RequestBody Role role) {
        return service.addRole(role);
    }

    @PutMapping("/add-role-to-user")
    public User addRoleToUser(@RequestBody @NotNull Form form) {
        User user = service.addRoleToUser(form.username(), form.roleName());
        return hideUserPassword(user);
    }

    @PutMapping("/remove-role-to-user")
    public User removeRoleToUser(@RequestBody @NotNull Form form) {
        User user = service.removeRoleToUser(form.username(), form.roleName());
        return hideUserPassword(user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUserById(@PathVariable Long id) {
        service.deleteUserById(id);
    }

    @GetMapping("/list")
    public List<User> getAllUsers() {
        List<User> users = service.getAllUsers();
        return users.stream().map(this::hideUserPassword).toList();
    }

    private User hideUserPassword(User user) {
        if (user == null) {
            return null;
        }
        user.setPassword(null);
        return user;
    }
}
