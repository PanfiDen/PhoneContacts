package com.chiacademy.phonecontacts.authentication.controller;

import com.chiacademy.phonecontacts.authentication.service.AuthenticationService;
import com.chiacademy.phonecontacts.authentication.model.request.RegistrationRequest;
import com.chiacademy.phonecontacts.authentication.model.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@RequestBody @Valid RegistrationRequest user) {
        return authenticationService.signUp(user);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse signIn(@Valid Authentication authentication) {
        return authenticationService.signIn(authentication.getName());
    }
}
