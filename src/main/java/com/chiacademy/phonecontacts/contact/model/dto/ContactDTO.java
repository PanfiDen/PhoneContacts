package com.chiacademy.phonecontacts.contact.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ContactDTO {
    @NotEmpty
    String name;
    Set<String> emails;
    Set<String> phones;
}
