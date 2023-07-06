package com.chiacademy.phonecontacts.user.service;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;

public interface UserService {
    Contact createContact(Long userId, ContactDTO newContact);
}
