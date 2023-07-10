package com.chiacademy.phonecontacts.authentication.service;

import com.chiacademy.phonecontacts.authentication.model.request.RegistrationRequest;
import com.chiacademy.phonecontacts.authentication.model.response.AuthResponse;
import com.chiacademy.phonecontacts.user.model.User;

public interface AuthenticationService {
    AuthResponse signUp(RegistrationRequest user);

    AuthResponse signIn(String email);

    boolean isNotCurrentUser(User user);
}
