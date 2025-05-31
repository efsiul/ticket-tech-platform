package com.tickettech.authservice;

import com.tickettech.authservice.models.Role;
import com.tickettech.authservice.models.User;
import com.tickettech.authservice.repositories.RoleRepository;
import com.tickettech.authservice.service.UserService;
import com.tickettech.authservice.service.implementation.LoadData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import java.util.List;

@SpringBootApplication
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(UserService userService, RoleRepository roleRepository, LoadData loadData) {
        return args -> {
            List<Role> roleList = roleRepository.findAll();
            List<User> userList = userService.getAllUsers();
            if (roleList.isEmpty() && userList.isEmpty()) {
                Role adminRole = new Role(1L, "ADMIN");
                Role userRole = new Role(2L, "USER");
                List<Role> rolesSaved = roleRepository.saveAll(
                        List.of(adminRole, userRole)
                );
                User user = User.builder().enabled(true).username("admin").password("rh2023-boc").email("admin@rhiscom.com").roles(rolesSaved).id(1L).build();
                userService.save(user);
            }
            loadData.seedData();
        };
    }
}
