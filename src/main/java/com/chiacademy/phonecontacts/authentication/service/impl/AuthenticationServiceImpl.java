package com.chiacademy.phonecontacts.authentication.service.impl;

import com.chiacademy.phonecontacts.authentication.model.request.RegistrationRequest;
import com.chiacademy.phonecontacts.authentication.model.response.AuthResponse;
import com.chiacademy.phonecontacts.authentication.service.AuthenticationService;
import com.chiacademy.phonecontacts.exception.exception.UserNotFoundException;
import com.chiacademy.phonecontacts.user.model.User;
import com.chiacademy.phonecontacts.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private JwtEncoder jwtEncoder;
    PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Override
    public AuthResponse signUp(RegistrationRequest user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new ResponseStatusException((HttpStatus.CONFLICT),
                    "Username is already taken.");
        }

        User newUser = User.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword())).build();
        userRepository.save(newUser);


        return AuthResponse.builder()
                .id(newUser.getId())
                .token(generateJwtToken(newUser.getId(), newUser.getEmail())).build();
    }

    @Override
    public AuthResponse signIn(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return AuthResponse.builder()
                .id(user.getId())
                .token(generateJwtToken(user.getId(), email)).build();
    }

    private String generateJwtToken(Long id, String email){
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(45, ChronoUnit.MINUTES))
                .subject(email)
                .claim("id", id)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
