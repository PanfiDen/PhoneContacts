package com.chiacademy.phonecontacts.contact.model.dto;

import com.chiacademy.phonecontacts.contact.model.entity.Email;
import com.chiacademy.phonecontacts.contact.model.entity.Phone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ContactDTO {
    String name;
    List<Email> emails;
    List<Phone> phones;
}
