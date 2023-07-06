package com.chiacademy.phonecontacts.authentication.service;

import com.chiacademy.phonecontacts.authentication.model.request.RegistrationRequest;
import com.chiacademy.phonecontacts.authentication.model.response.AuthResponse;

public interface AuthenticationService {
    AuthResponse signUp(RegistrationRequest user);

    AuthResponse signIn(String email);
}
