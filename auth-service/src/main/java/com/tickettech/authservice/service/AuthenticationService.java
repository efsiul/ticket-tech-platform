package com.tickettech.authservice.service;

import com.tickettech.authservice.dto.AuthenticationRequest;
import com.tickettech.authservice.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception;

    AuthenticationResponse authenticateInternal(AuthenticationRequest request) throws Exception;
}
