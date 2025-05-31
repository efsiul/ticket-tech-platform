package com.tickettech.authservice.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tickettech.authservice.dto.AuthenticationRequest;
import com.tickettech.authservice.dto.AuthenticationResponse;
import com.tickettech.authservice.models.User;
import com.tickettech.authservice.repositories.UserRepository;
import com.tickettech.authservice.security.SecParams;
import com.tickettech.authservice.service.AuthenticationService;
import com.tickettech.authservice.utils.utilities;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final SecParams secParams;

        public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                                         SecParams secParams) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.secParams = secParams;
        }

        @Override
        public AuthenticationResponse authenticate(@NotNull AuthenticationRequest request) throws Exception {
                log.info("In authenticate()");
                String lsPassword = utilities.decrypt(request.password());
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(), lsPassword));
                User user = userRepository.findByUsername(request.username());
                List<String> roles = new ArrayList<>();
                user.getRoles().forEach(
                        role -> roles.add(role.getName()));
                String jwt = JWT.create().withSubject(user.getUsername())
                        .withArrayClaim("roles", roles.toArray(new String[0]))
                        .withExpiresAt(new Date(System.currentTimeMillis() + secParams.getExpiredTime()))
                        .sign(Algorithm.HMAC256(secParams.getSecret()));
                log.info("user authenticated");
                return new AuthenticationResponse(jwt, user.getUsername());
        }

        @Override
        public AuthenticationResponse authenticateInternal(@NotNull AuthenticationRequest request) throws Exception {
                log.info("In authenticate()");
                String lsPassword = utilities.decrypt(request.password());
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(), lsPassword));
                User user = userRepository.findByUsername(request.username());
                List<String> roles = new ArrayList<>();
                user.getRoles().forEach(
                        role -> roles.add(role.getName()));
                String jwt = JWT.create().withSubject(user.getUsername())
                        .withArrayClaim("roles", roles.toArray(new String[0]))
                        .withExpiresAt(new Date(System.currentTimeMillis() + secParams.getExpiredTime()))
                        .sign(Algorithm.HMAC256(secParams.getSecret()));
                log.info("user authenticated");
                return new AuthenticationResponse(jwt, user.getUsername());
        }
}
