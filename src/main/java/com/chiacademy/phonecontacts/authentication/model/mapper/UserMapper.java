package com.chiacademy.phonecontacts.authentication.model.mapper;


import com.chiacademy.phonecontacts.user.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.userdetails.User.withUsername;

@Component
public class UserMapper{
    public UserDetails toUserDetails(User userEntity) {
        return withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}