package com.chiacademy.phonecontacts.authentication.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    @NotEmpty
    @Email
    String email;
    @NotEmpty
    String password;
}
